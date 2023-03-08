package mod.hilal.saif.components;

import org.json.JSONArray;

import a.a.a.Hx;
import mod.agus.jcoderz.lib.FileUtil;

public class ComponentExtraCode {

    private final StringBuilder b;
    private final Hx hx;
    private final JSONArray listeners;

    public ComponentExtraCode(Hx h, StringBuilder st) {
        hx = h;
        b = st;
        listeners = getListenersJsonArray();
    }

    public void s(String str) {
        // Aldi's original Components
        if (str.contains("DatePickerFragment")) {
            hx.l = str;
            return;
        }
        if (str.contains("FragmentStatePagerAdapter")) {
            if (hx.k.isEmpty()) {
                hx.k = str;
            } else {
                hx.k = hx.k.concat("\r\n\r\n").concat(str);
            }
            return;
        }
        if (str.contains("extends AsyncTask<String, Integer, String>")) {
            if (hx.k.isEmpty()) {
                hx.k = str;
            } else {
                hx.k = hx.k.concat("\r\n\r\n").concat(str);
            }
            return;
        }

        // Hilal's components
        String firstLine = getFirstLine(str);
        for (int i = 0; i < listeners.length(); i++) {
            try {
                String c = listeners.getJSONObject(i).getString("code");
                if (!listeners.getJSONObject(i).isNull("s") && str.contains(firstLine)) {
                    String q = listeners.getJSONObject(i).getString("s");
                    if (q.equals("true")) {
                        if (hx.k.isEmpty()) {
                            hx.k = str.replace(firstLine, "");
                        } else {
                            hx.k = hx.k.concat("\r\n\r\n").concat(str.replace(firstLine, ""));
                        }
                        return;
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }

        //others
        if (b.length() > 0 && str.length() > 0) {
            b.append("\r\n\r\n");
        }
        b.append(str);
    }

    private JSONArray getListenersJsonArray() {
        String path = FileUtil.getExternalStorageDir().concat("/.sketchware/data/system/listeners.json");
        try {
            String jsonStr = FileUtil.readFile(path);
            if (!jsonStr.isEmpty()) {
                return new JSONArray(jsonStr);
            }
        } catch (Exception e) {
            // ignore
        }
        return new JSONArray();
    }

    private String getFirstLine(String str) {
        if (str.contains("\n")) {
            return str.substring(0, str.indexOf("\n")).trim();
        } else {
            return str.trim();
        }
    }
}
