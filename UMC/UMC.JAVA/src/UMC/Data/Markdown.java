package UMC.Data;


import UMC.Web.UICell;
import UMC.Web.UIStyle;
import UMC.Web.WebMeta;
import UMC.Web.UIClick;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class Markdown {
    private WebMeta webRel = new WebMeta();
    private List<UICell> cells = new LinkedList<>();
    private WebMeta data = new WebMeta();
    private UIStyle style = new UIStyle();
    private StringBuilder dataText = new StringBuilder();

    private Markdown() {

    }

    private void Header(String text) {
        int i = 0;
        while (i < text.length() && text.charAt(i) == '#') {
            i++;
            if (i == 6) {
                break;
            }
        }
        int size = 26 - (i - 1) * 2;
        dataText.append(text.substring(i).trim());
        style.name("m" + data.size(), new UIStyle().bold().size(size));


        append();
    }

    static String[] keys = new String[]{"var", "instanceof", "extern", "private", "protected", "public", "namespace", "class", "for", "if", "else", "while", "switch", "case", "using", "get", "return", "null", "void", "int", "string", "float", "char", "this", "set", "new", "true", "false", "const", "static", "internal", "extends", "super", "import", "default", "break", "try", "catch", "finally", "implements", "package", "final"};

    private static class Highlighter {

        WebMeta data = new WebMeta();
        UIStyle style = new UIStyle();
        StringBuilder dataText = new StringBuilder();


        public UICell Transform(String text) {
            check(text);
            append();
            if (dataText.length() > 0) {
                data.put("h" + data.size(), dataText.toString());
            }
            dataText = new StringBuilder();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.size(); i++) {
                sb.append("{h");
                sb.append(i);
                sb.append("}");
            }
            UICell cell = UICell.create("CMSCode", data);
            cell.format("text", sb.toString());
            cell.style().copy(style);

            return cell;

        }

        public void checkWork() {
            String value = dataText.toString();
            for (String k : keys) {
                if (value.endsWith(k)) {
                    if (value.length() > k.length()) {
                        switch (value.charAt(value.length() - k.length() - 1)) {
                            case '.':
                            case ' ':
                            case '\n':
                            case '(':
                            case '[':
                            case '{':
                            case '\t':
                                dataText.delete(value.length() - k.length(), value.length());
                                data.put("h" + data.size(), dataText.toString());

                                style.name("h" + data.size()).color(0x00f);
                                data.put("h" + data.size(), k);
                                dataText.delete(0, dataText.length());
                                break;
                        }
                    } else {

                        style.name("h" + data.size()).color(0x00f);
                        data.put("h" + data.size(), dataText.toString());
                        dataText.delete(0, dataText.length());
                    }
                }
            }
        }

        public void append() {
            checkWork();
            if (dataText.length() > 0) {

                data.put("h" + data.size(), dataText.toString());
                dataText.delete(0, dataText.length());
            }
        }

        void check(String code) {
            int index = 0;
            while (index < code.length()) {
                switch (code.charAt(index)) {
                    case '"': {
                        append();
                        int end = code.indexOf('"', index + 1);

                        style.name("h" + data.size()).color(0xc00);
                        if (end == -1) {
                            end = code.indexOf('\n', index);
                            if (end == -1) {
                                data.put("h" + data.size(), code.substring(index));
                                return;
                            } else {

                                data.put("h" + data.size(), code.substring(end - index + 1));
                                index = end + 1;

                                continue;
                            }
                        } else {
                            while (code.charAt(end - 1) == '\\') {

                                end = code.indexOf('"', end + 1);
                                if (end == -1) {
                                    data.put("h" + data.size(), code.substring(index));
                                    return;
                                }
                            }

                            data.put("h" + data.size(), code.substring(index, end + 1));
                        }
                        index = end + 1;

                        continue;
                    }
                    case '\'': {
                        append();
                        int end = code.indexOf('\'', index + 1);

                        if (end > 0) {
                            style.name("h" + data.size()).color(0xc00);

                            data.put("h" + data.size(), code.substring(index, end + 1));

                            index = end + 1;
                            continue;

                        }
                    }
                    break;
                    case '/':
                        switch (code.charAt(index + 1)) {
                            case '/': {
                                append();
                                if (dataText.length() > 0) {
                                    data.put("h" + data.size(), dataText.toString());
                                }
                                dataText.delete(0, dataText.length());
                                style.name("h" + data.size()).color(0x008000);
                                int end = code.indexOf('\n', index);
                                if (end == -1) {

                                    data.put("h" + data.size(), code.substring(index));

                                    return;
                                } else {

                                    data.put("h" + data.size(), code.substring(index, end));
                                }
                                index = end;
                            }
                            continue;
                            case '*': {
                                append();
                                if (dataText.length() > 0) {
                                    data.put("h" + data.size(), dataText.toString());
                                }
                                dataText.delete(0, dataText.length());
                                style.name("h" + data.size()).color(0x008000);
                                int end = code.indexOf("*/", index);
                                if (end == -1) {

                                    data.put("h" + data.size(), code.substring(index));
                                    return;
                                } else {

                                    data.put("h" + data.size(), code.substring(index, end + 2));
                                }
                                index = end + 2;
                                continue;
                            }
                        }
                        break;
                    case ' ':
                    case '.':
                        checkWork();
                        break;
                }
                dataText.append(code.charAt(index));
                index++;
            }

        }
    }

    public static UICell[] transform(String text) {
        Markdown markdown = new Markdown();
        markdown.check(text, 0);
        for (UIClick click : markdown.links) {
            click.send(markdown.webRel.get((String) click.send()));
        }
        for (WebMeta meta : markdown.webRels) {
            meta.put("src", markdown.webRel.get(meta.get("src")));
        }
        return markdown.cells.toArray(new UICell[0]);

    }

    void appendData() {
        if (dataText.length() > 0) {

            data.put("m" + data.size(), dataText.toString());

            dataText = new StringBuilder();

        }
    }

    private void append() {
        if (data.size() > 0 || dataText.length() > 0) {
            if (dataText.length() > 0) {
                data.put("m" + data.size(), dataText.toString().trim());
            }
            dataText = new StringBuilder();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.size(); i++) {
                sb.append("{m");
                sb.append(i);
                sb.append("}");
            }
            UICell cell = UICell.create(Utility.isNull(data.get("type"), "CMSText"), data);
            data.remove("type");
            cell.format("text", sb.toString());
            cell.style().copy(style);
            cells.add(cell);
            data = new WebMeta();
            style = new UIStyle();
        }
    }

    private List<WebMeta> webRels = new LinkedList<>();
    private List<UIClick> links = new LinkedList<>();

    private void checkRow(String text, int index) {
        checkRow(text, index, true);
    }

    void checkRow(String text, int index, boolean isNextLIne) {
        while (index + 1 < text.length() && text.charAt(index) != '\n') {

            switch (text.charAt(index)) {
                case '\r':
                    index++;
                    continue;
                case '!':
                    if (text.charAt(index + 1) == '[' && isNextLIne) {
                        int end = text.indexOf("]", index + 1);
                        if (end > index) {

                            String content = text.substring(index + 2, end).trim();
                            if (content.indexOf('\n') == -1) {
                                if (text.charAt(end + 1) == '(') {
                                    append();
                                    int end2 = text.indexOf(")", end + 1);
                                    if (end2 > end) {
                                        String url = text.substring(end + 1, end2).trim().split(" ")[0];
                                        UICell cell = UICell.create("CMSImage", new WebMeta().put("src", url));
                                        cell.style().padding(0, 10);
                                        cells.add(cell);
                                        index = end2 + 1;

                                        continue;
                                    }
                                } else {
                                    append();
                                    if (webRel.containsKey(content)) {

                                        UICell cell = UICell.create("CMSImage", new WebMeta().put("src", webRel.get(content)));
                                        cell.style().padding(0, 10);
                                        cells.add(cell);
                                    } else {
                                        WebMeta r = new WebMeta().put("src", content);
                                        webRels.add(r);
                                        UICell cell = UICell.create("CMSImage", r);
                                        cell.style().padding(0, 10);
                                        cells.add(cell);
                                    }
                                    index = end + 1;

                                    continue;
                                }

                            }

                        }
                    }
                    break;
                case '[': {
                    int end = text.indexOf("]", index + 1);
                    if (end > index) {
                        String content = text.substring(index + 1, end);//.trim('[', ']');
                        if (content.indexOf('\n') == -1) {
                            if (text.charAt(end + 1) == '(') {

                                int end2 = text.indexOf(")", end + 1);
                                if (end2 > end) {
                                    String url = text.substring(end + 2, end2).trim().split(" ")[0];

                                    appendData();
                                    style.name("m" + data.size(), new UIStyle().click(new UIClick(url).key("Url")));
                                    data.put("m" + data.size(), content);

                                    index = end2 + 1;

                                    continue;
                                }
                            } else {

                                appendData();

                                if (webRel.containsKey(content)) {
                                    style.name("m" + data.size(), new UIStyle().click(new UIClick(webRel.get(content)).key("Url")));

                                } else {
                                    UIClick click = new UIClick(webRel.get(content)).key("Url");
                                    links.add(click);

                                    style.name("m" + data.size(), new UIStyle().click(click));
                                }
                                data.put("m" + data.size(), content);


                                index = end + 1;

                                continue;
                            }

                        }
                    }
                }
                break;
                case '`': {

                    int end = text.indexOf("`", index + 1);
                    if (end > index) {
                        String content = text.substring(index + 1, end);
                        if (content.indexOf('\n') == -1) {

                            appendData();
                            style.name("m" + data.size(), new UIStyle().color(0xCC6600));
                            data.put("m" + data.size(), content);


                            index = end + 1;

                            continue;
                        }
                    }
                }
                break;
                case '~':
                    if (text.charAt(index + 1) == '~') {

                        int end = text.indexOf("~~", index + 1);
                        if (end > index) {

                            String content = text.substring(index + 2, end);
                            if (content.indexOf('\n') == -1) {

                                appendData();
                                style.name("m" + data.size(), new UIStyle().delLine());
                                data.put("m" + data.size(), content);

                                index = end + 2;

                                continue;

                            }
                        }

                    } else {
                        int end = text.indexOf("~", index + 1);
                        if (end > index) {
                            String content = text.substring(index + 1, end);
                            if (content.indexOf('\n') == -1) {

                                appendData();
                                style.name("m" + data.size(), new UIStyle().underLine());
                                data.put("m" + data.size(), content);
                                index = end + 1;

                                continue;


                            }
                        }

                    }
                    break;
                case '*':
                    if (text.charAt(index + 1) == '*') {

                        int end = text.indexOf("**", index + 1);

                        if (end > index) {
                            String content = text.substring(index + 2, end);
                            if (content.indexOf('\n') == -1) {

                                appendData();
                                style.name("m" + data.size(), new UIStyle().bold());
                                data.put("m" + data.size(), content);

                                index = end + 2;

                                continue;

                            }
                        }

                    } else {
                        int end = text.indexOf("*", index + 1);
                        if (end > index) {
                            String content = text.substring(index + 1, end);
                            if (content.indexOf('\n') == -1) {

                                appendData();
                                style.name("m" + data.size(), new UIStyle().underLine());
                                data.put("m" + data.size(), content);
                                index = end + 1;

                                continue;

                            }
                        }

                    }
                    break;
            }
            dataText.append(text.charAt(index));
            index++;
        }
        if (index + 1 == text.length()) {
            dataText.append(text.charAt(index));
        }
        if (isNextLIne) {
            append();
            check(text, index + 1);
        }

    }

    private void Grid(List<String> rows) {
        List<UIStyle> hStyle = new LinkedList<>();
        String[] header = Utility.trim(rows.get(1), '|').split("\\|");
        List<List<WebMeta>> grid = new LinkedList<>();
        int flexs = 0;
        for (String h : header) {
            UIStyle st = new UIStyle();
            String s = h.trim();
            if (s.startsWith(":") && s.endsWith(":")) {
                st.alignCenter();
            } else if (s.endsWith(":")) {
                st.alignRight();
            } else {
                st.alignLeft();
            }
            int flex = s.split("-").length - 1;
            st.name("flex", flex);
            flexs += flex;
            hStyle.add(st);
        }
        rows.remove(1);
        for (String row : rows) {
            String[] cells = Utility.trim(row, '|').split("\\|");
            List<WebMeta> cdata = new LinkedList<>();
            for (int i = 0; i < hStyle.size(); i++) {
                UIStyle cstyle = new UIStyle();
                cstyle.copy(hStyle.get(i));
                this.style = cstyle;
                this.data = new WebMeta();
                this.dataText = new StringBuilder();
                if (i < cells.length) {
                    checkRow(cells[i].trim(), 0, false);
                }
                if (dataText.length() > 0) {
                    this.data.put("m" + data.size(), dataText.toString().trim());
                }
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < data.size(); c++) {
                    sb.append("{m");
                    sb.append(c);
                    sb.append("}");
                }
                cdata.add(new WebMeta().put("format", sb.toString()).put("data", this.data).put("style", this.style));

            }
            grid.add(cdata);
        }

        UICell cell = UICell.create("CMSGrid", new WebMeta().put("grid", grid).put("flex", flexs));
        cells.add(cell);
        data = new WebMeta();
        style = new UIStyle();
        dataText = new StringBuilder();

    }

    private void check(String text, int index) {
        if (index + 1 >= text.length()) {
            append();
            return;
        }
        switch (text.charAt(index)) {
            case '#': {
                int end = text.indexOf('\n', index);
                if (end > index) {
                    Header(text.substring(index, end));
                    check(text, end + 1);
                } else {
                    Header(text.substring(index));
                }
                return;
            }
            case '`': {
                if (text.substring(index, index + 3).equals("```")) {
                    int end = text.indexOf("\n```", index + 1);
                    if (end > index) {
                        String content = text.substring(index, end);

                        content = content.substring(content.indexOf('\n') + 1);

                        cells.add(new Highlighter().Transform(content));
                        index = text.indexOf('\n', end + 1) + 1;
                        check(text, index);
                        return;
                    }
                }
            }
            break;
            case '>': {
                if (cells.size() > 0) {
                    UICell cell = cells.get(cells.size() - 1);
                    if (cell.type().equals("CMSRel")) {
                        WebMeta d = (WebMeta) cell.data();//as WebMeta;

                        cells.remove(cells.size() - 1);
                        this.data = d;
                        this.dataText.append("\r\n");
                        this.style = cell.style();
                        this.data.put("type", "CMSRel");
                        checkRow(text, index + 1);
                        return;

                    }
                }
                this.data.put("type", "CMSRel");
                checkRow(text, index + 1);
                return;
            }
            case '|': {

                int end = text.indexOf('\n', index);
                if (end > index) {
                    List<String> grids = new LinkedList<>();
                    String conent = text.substring(index, end).trim();
                    if (conent.charAt(conent.length() - 1) == '|') {
                        int end2 = text.indexOf('\n', end + 1);//.Trim();
                        String conent2 = text.substring(end + 1, end2).trim().replace(" ", "");
                        grids.add(conent);

                        if (Pattern.matches("^[\\|:-]+$", conent2)) {
                            grids.add(conent2);
                            if (conent2.split("\\|").length == conent.split("\\|").length) {
                                boolean isGO = true;
                                while (isGO) {
                                    isGO = false;
                                    int end3 = text.indexOf('\n', end2 + 1);//.Trim();

                                    String conent3 = end3 > 0 ? text.substring(end2 + 1, end3).trim() : text.substring(end2 + 1).trim();
                                    if (conent3.startsWith("|") && conent3.endsWith("|")) {
                                        isGO = true;
                                        grids.add(conent3);
                                        end2 = end3;
                                    }
                                }
                                this.Grid(grids);
                                check(text, end2 + 1);
                                return;

                            }
                        }

                    }
                }
            }
            break;
            case '[': {
                int end = text.indexOf(']', index + 1);
                if (end > index && end + 1 < text.length()) {
                    if (text.charAt(end + 1) == ':') {
                        String content = text.substring(index + 1, end);//.trim('[', ']');
                        if (content.indexOf('\n') == -1) {
                            int end2 = text.indexOf("\n", end + 1);
                            if (end2 == -1) {

                                String url = Utility.trim(text.substring(end + 2).trim(), '(', ')').split(" ")[0];
                                webRel.put(content, url);
                            } else {
                                String url = Utility.trim(text.substring(end + 2, end2).trim(), '(', ')').split(" ")[0];
                                webRel.put(content, url);
                                check(text, end2 + 1);
                            }
                            return;

                        }


                    }
                }
            }
            break;
            case ' ': {
                while (text.length() > index && text.charAt(index) == ' ') index++;
            }
            break;

        }

        this.checkRow(text, index);
    }
}