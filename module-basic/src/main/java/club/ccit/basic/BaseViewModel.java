package club.ccit.basic;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * FileName: BaseViewModel
 *
 * @author: 张帅威
 * Date: 2025/1/12 3:49 下午
 * Description: ViewModel 基类
 * Version:
 */
public class BaseViewModel extends ViewModel {
    public MutableLiveData<String> message = new MutableLiveData<>();
    public MutableLiveData<Integer> networkError = new MutableLiveData<>();
    public MutableLiveData<Boolean> ok = new MutableLiveData<>();
    public final int SUCCESS = 200;

    @Override
    protected void onCleared() {
        super.onCleared();
        clear();
    }

    protected void clear() {

    }
}
