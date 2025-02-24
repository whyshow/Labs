package club.ccit.sdk.net;

import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * FileName: NullOnEmptyConverterFactory
 *
 * @author: 张帅威
 * Date: 2022/8/2 10:36
 * Description: 解决body为空时的 End of input at line 1 column 1
 * Version:
 */
public class NullOnEmptyConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
        return new Converter<ResponseBody, Object>() {
            @Override
            public Object convert(ResponseBody body) throws IOException {
                Log.d("log111","NullOnEmptyConverterFactory");
                if (body.contentLength() == 0){
                    return null;
                }
                return delegate.convert(body);
            }
        };
    }
}
