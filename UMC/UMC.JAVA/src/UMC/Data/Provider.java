package UMC.Data;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Provider implements IJSON {
    // Fields
    private Map<String, String> _ProviderAttributes = new HashMap<>();
    private String _ProviderName;
    private String _ProviderType;

    private Provider() {
    }

    public static Provider create(String name, String type) {
        Provider p = new Provider();
        p._ProviderName = name;
        p._ProviderType = type;
        return p;
    }

    // Methods
    Provider(NamedNodeMap attr) {
        this._ProviderName = attr.getNamedItem("name").getNodeValue();
        Node type = attr.getNamedItem("type");

        this._ProviderType = type == null ? "" : type.getNodeValue();

        for (int i = 0, l = attr.getLength(); i < l; i++) {
            Node node = attr.item(i);
            switch (node.getNodeName()) {
                case "name":
                case "type":
                    break;
                default:
                    _ProviderAttributes.put(node.getNodeName(), node.getNodeValue());
                    break;
            }
        }

    }

    public Provider(Node node)

    {
        this(node.getAttributes());
        if (node.hasChildNodes()) {
            NodeList list = node.getChildNodes();
            for (int i = 0, l = list.getLength(); i < l; i++) {
                Node c = list.item(i);

                _ProviderAttributes.put(c.getNodeName(), c.getNodeValue());
            }
        }
    }

    /// <summary>
    /// 属性
    /// </summary>
    public Map<String, String> attributes() {

        return this._ProviderAttributes;

    }

    public String get(String name) {
        return _ProviderAttributes.get(name);
    }

    /// <summary>
    /// 名称
    /// </summary>
    public String name() {
        return this._ProviderName;

    }

    /// <summary>
    /// 提交的类型
    /// </summary>
    public String type() {

        return this._ProviderType;

    }

    @Override
    public void write(Writer writer) {
        try {
            writer.write("{");
            writer.write("\"name\":");
            JSON.serialize(this._ProviderName, writer);
            writer.write(',');

            writer.write("\"type\":");

            JSON.serialize(this._ProviderType, writer);
            Iterator em = _ProviderAttributes.keySet().iterator();
            boolean bo = false;
            while (em.hasNext()) {
                if (bo) {
                    writer.write(',');
                } else {
                    bo = true;
                }
                Object key = em.next();
                JSON.serialize(key, writer);
                writer.write(':');
                JSON.serialize(_ProviderAttributes.get(key), writer);

            }
            writer.write('}');
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void read(String key, Object value) {
        switch (key) {
            case "name":
                this._ProviderName = (value instanceof String) ? (String) value : value.toString();
                break;
            case "type":
                this._ProviderType = (value instanceof String) ? (String) value : value.toString();
                break;
            default:
                this._ProviderAttributes.put(key, (value instanceof String) ? (String) value : value.toString());
                break;
        }

    }
}
