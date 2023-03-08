package mod.hilal.saif.components;

import org.json.JSONArray;
import java.lang.StringBuilder;
import a.a.a.Hx;
import mod.agus.jcoderz.lib.FileUtil;

public class ComponentExtraCode {

    private final StringBuilder b;
    private final Hx hx;
    private final JSONArray listeners;
    private final int listenersLength;

    public ComponentExtraCode(Hx h, StringBuilder st) {
        hx = h;
        b = st;
        listeners = getListenersJsonArray();
        listenersLength = listeners.length();
    }

    public void s(String str) {
        // Aldi's original Components
        if (str.startsWith("DatePickerFragment")) {
            hx.l = str;
            return;
        }
        if (str.startsWith("FragmentStatePagerAdapter")) {
            if (hx.k.isEmpty()) {
                hx.k = str;
            } else {
                hx.k = hx.k.append("\r\n\r\n").append(str);
            }
            return;
        }
        if (str.startsWith("extends AsyncTask<String, Integer, String>")) {
            if (hx.k.isEmpty()) {
                hx.k = str;
            } else {
                hx.k = hx.k.append("\r\n\r\n").append(str);
            }
            return;
        }

        // Hilal's components
        String firstLine = getFirstLine(str);
        for (int i = 0; i < listenersLength; i++) {
            String c = listeners.getJSONObject(i).optString("code");
            if (c != null && str.startsWith(firstLine)) {
                String q = listeners.getJSONObject(i).optString("s");
                if ("true".equals(q)) {
                    if (hx.k.isEmpty()) {
                        hx.k = str.substring(firstLine.length());
                    } else {
                        hx.k = hx.k.append("\r\n\r\n").append(str.substring(firstLine.length()));
                    }
                    return;
                }
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
        int pos = str.indexOf('\n');
        if (pos != -1) {
            return str.substring(0, pos).trim();
        } else {
            return str.trim();
        }
    }
}
