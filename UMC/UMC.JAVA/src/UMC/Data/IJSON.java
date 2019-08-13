package UMC.Data;

import java.io.OutputStream;
import java.io.Writer;

public interface IJSON {

    /// <summary>
    /// 序列化
    /// </summary>
    /// <param name="writer"></param>
    void write(Writer writer);
    /// <summary>
    /// 反序列化
    /// </summary>
    /// <param name="key">属性名</param>
    /// <param name="value">属性值</param>
    void read(String key, Object value);
}
