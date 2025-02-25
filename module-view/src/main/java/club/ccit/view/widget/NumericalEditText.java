package club.ccit.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import club.ccit.view.R;
import club.ccit.view.widget.utils.DecimalPointFilter;

public class NumericalEditText extends AppCompatEditText {
    private float maxValue = Integer.MAX_VALUE; // 默认最大值为Integer.MAX_VALUE
    private float minValue = Integer.MIN_VALUE; // 默认最小值为Integer.MIN_VALUE
    private boolean pasteAllowed = true; // 默认允许粘贴
    private boolean copyAllowed = true; // 默认允许复制
    private boolean validateInput = false; // 默认不允许验证输入
    private boolean errTip = false; // 默认错误验证不提示
    private boolean correct = false; // 默认不强制校准
    private View parentView; // 父控件
    private Drawable parentBackgroundErr; // 父控件的背景
    private Drawable parentCurrentBackground; // 父控件原本的背景
    private int decimalDigits = -1; // -1表示不限制

    public NumericalEditText(@NonNull Context context) {
        super(context);
    }

    public NumericalEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
        // 输入校验
        init();
    }

    public NumericalEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(attrs);
        // 输入校验
        init();
    }

    private void initAttributes(AttributeSet attrs) {
        // 从XML属性读取配置
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomEditText);
        maxValue = a.getFloat(R.styleable.CustomEditText_customMaxValue, Float.MAX_VALUE);
        minValue = a.getFloat(R.styleable.CustomEditText_customMinValue, Float.MIN_VALUE);
        pasteAllowed = a.getBoolean(R.styleable.CustomEditText_customPasteAllowed, true);
        copyAllowed = a.getBoolean(R.styleable.CustomEditText_customCopyAllowed, true);
        validateInput = a.getBoolean(R.styleable.CustomEditText_customValidateInput, false);
        errTip = a.getBoolean(R.styleable.CustomEditText_customErrTip, false);
        correct = a.getBoolean(R.styleable.CustomEditText_customCorrect, false);
        decimalDigits = a.getInt(R.styleable.CustomEditText_customDecimalDigits, -1);
        int parentId = a.getResourceId(R.styleable.CustomEditText_customParentId, View.NO_ID);
        parentBackgroundErr = a.getDrawable(R.styleable.CustomEditText_customParentBackgroundErr);
        parentCurrentBackground = a.getDrawable(R.styleable.CustomEditText_customParentCurrentBackground);
        if (parentId != View.NO_ID) {
            ViewParent parent = getParent();
            if (parent instanceof ViewGroup) {
                parentView = ((ViewGroup) parent).findViewById(parentId);
            }
        }
        a.recycle();
    }

    // 初始化方法
    private void init() {
        switch (getInputType()) {
            case InputType.TYPE_CLASS_NUMBER:
            case (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL):
                if (decimalDigits != -1) {
                    InputFilter[] vEditTextFilters = new InputFilter[]{new DecimalPointFilter(decimalDigits)};
                    setFilters(vEditTextFilters);
                }
                addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (validateInput) {
                            if (parentView != null && parentBackgroundErr != null) {
                                parentView.setBackground(validateInput() ? parentCurrentBackground : parentBackgroundErr);
                            } else {
                                setBackground(validateInput() ? parentCurrentBackground : parentBackgroundErr);
                            }
                        }
                    }
                });
                break;
        }
    }

    // 检查输入是否合法
    private boolean validateInput() {
        if (!isScope()) {
            // 不在范围内
            if (errTip) {
                if (getInputType() == InputType.TYPE_CLASS_NUMBER) {
                    setError((int) minValue + "~" + (int) maxValue);
                } else if (getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)) {
                    setError(minValue + "~" + maxValue);
                }
            }
            if (correct && (getInputType() == InputType.TYPE_CLASS_NUMBER || getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL))) {
                setText(String.valueOf((int) minValue));
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (!pasteAllowed && id == android.R.id.paste) return false;
        if (!copyAllowed && id == android.R.id.copy) return false;
        return super.onTextContextMenuItem(id);
    }

    // 设置最大值的public方法
    public void setMaxValue(int max) {
        this.maxValue = max;
    }

    // 设置最小值的public方法
    public void setMinValue(int max) {
        this.minValue = max;
    }

    // 控制粘贴权限的public方法
    public void setPasteAllowed(boolean allowed) {
        this.pasteAllowed = allowed;
    }

    // 控制拷贝权限的public方法
    public void setCopyAllowed(boolean allowed) {
        this.copyAllowed = allowed;
    }

    // 检查输入的public方法
    public void checkInput(boolean check) {
        this.correct = check;
        validateInput();
    }

    // 检查范围
    private boolean isScope() {
        try {
            String input = getText().toString().trim();
            if (input.isEmpty()) return false;

            if ((getInputType() & InputType.TYPE_CLASS_NUMBER) != 0) {
                int value = Integer.parseInt(input);
                return (minValue == Integer.MIN_VALUE || value >= minValue) &&
                        (maxValue == Integer.MAX_VALUE || value <= maxValue);
            }
            if ((getInputType() & (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)) != 0) {
                double value = Double.parseDouble(input);
                return (minValue == Integer.MIN_VALUE || value >= minValue) &&
                        (maxValue == Integer.MAX_VALUE || value <= maxValue);
            }
        } catch (NumberFormatException e) {
            // 非数字输入视为超出范围
        }
        return false;
    }
}
