package com.besome.sketch.editor.property;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.lang.InterruptedException;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.besome.sketch.beans.ProjectFileBean;
import com.besome.sketch.editor.LogicEditorActivity;
import com.sketchware.remod.R;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import a.a.a.Kw;
import a.a.a.OB;
import a.a.a.SB;
import a.a.a.TB;
import a.a.a._B;
import a.a.a.aB;
import a.a.a.jC;
import a.a.a.mB;
import a.a.a.uq;
import a.a.a.wB;
import mod.hey.studios.util.Helper;
import mod.hilal.saif.asd.asdforall.AsdAllEditor;
// new
import dev.derecky.sany.editor.tools.translateapi.*;


@SuppressLint("ViewConstructor")
public class PropertyInputItem extends RelativeLayout implements View.OnClickListener {

    private Context context;
    private String key = "";
    private String value = "";
    private ImageView imgLeftIcon;
    private int icon;
    private int icon2;
    private LogicEditorActivity logicEditor;
    private TextView tvName;
    private TextView tvValue;
    private View propertyItem;
    private View propertyMenuItem;
    private String sc_id;
    private ProjectFileBean projectFileBean;
    private Kw valueChangeListener;

    public PropertyInputItem(Context context, boolean z) {
        super(context);
        initialize(context, z);
    }

    private void setIcon(ImageView imageView) {
        switch (key) {
            case "property_id":
                icon = R.drawable.rename_96_blue;
                break;

            case "property_text":
                icon = R.drawable.abc_96;
                icon2 = R.drawable.language_translate_96;
                break;

            case "property_hint":
                icon = R.drawable.help_96_blue;
                break;

            case "property_weight":
            case "property_weight_sum":
                icon = R.drawable.one_to_many_48;
                break;

            case "property_rotate":
                icon = R.drawable.ic_reset_color_32dp;
                break;

            case "property_lines":
            case "property_max":
            case "property_progress":
                icon = R.drawable.numbers_48;
                break;

            case "property_alpha":
                icon = R.drawable.opacity_48;
                break;

            case "property_translation_x":
                icon = R.drawable.swipe_right_48;
                break;

            case "property_translation_y":
                icon = R.drawable.swipe_down_48;
                break;

            case "property_scale_x":
            case "property_scale_y":
                icon = R.drawable.resize_48;
                break;

            case "property_inject":
                icon = R.drawable.ic_property_inject;
                icon2 = R.drawable.language_translate_96;
                break;

            case "property_convert":
                icon = R.drawable.ic_property_convert;
                break;
        }
        imageView.setImageResource(icon);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
        int identifier = getResources().getIdentifier(key, "string", getContext().getPackageName());
        if (identifier > 0) {
            tvName.setText(Helper.getResString(identifier));
            if (propertyMenuItem.getVisibility() == VISIBLE) {
                setIcon(findViewById(R.id.img_icon));
                ((TextView) findViewById(R.id.tv_title)).setText(Helper.getResString(identifier));
                return;
            }
            setIcon(imgLeftIcon);
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        tvValue.setText(value);
    }

    @Override
    public void onClick(View v) {
        if (!mB.a()) {
            switch (key) {
                case "property_id":
                    showViewIdDialog();
                    return;

                case "property_text":
                case "property_hint":
                case "property_inject":
                    showTextInputDialog(0, 9999);
                    return;

                case "property_weight":
                case "property_weight_sum":
                case "property_rotate":
                case "property_lines":
                case "property_max":
                case "property_progress":
                    showNumberInputDialog();
                    return;

                case "property_alpha":
                    showNumberDecimalInputDialog(0, 1);
                    return;

                case "property_translation_x":
                case "property_translation_y":
                    showNumberDecimalInputDialog(-9999, 9999);
                    return;

                case "property_scale_x":
                case "property_scale_y":
                    showNumberDecimalInputDialog(0, 99);
                    return;

                case "property_convert":
                    showTextInputDialog(0, 99);
            }
        }
    }

    public void setOnPropertyValueChangeListener(Kw onPropertyValueChangeListener) {
        valueChangeListener = onPropertyValueChangeListener;
    }

    public void setOrientationItem(int orientationItem) {
        if (orientationItem == 0) {
            propertyItem.setVisibility(GONE);
            propertyMenuItem.setVisibility(VISIBLE);
        } else {
            propertyItem.setVisibility(VISIBLE);
            propertyMenuItem.setVisibility(GONE);
        }
    }

    private void initialize(Context context, boolean z) {
        this.context = context;
        wB.a(context, this, R.layout.property_input_item);
        tvName = findViewById(R.id.tv_name);
        tvValue = findViewById(R.id.tv_value);
        imgLeftIcon = findViewById(R.id.img_left_icon);
        propertyItem = findViewById(R.id.property_item);
        propertyMenuItem = findViewById(R.id.property_menu_item);
        if (z) {
            setSoundEffectsEnabled(true);
            setOnClickListener(this);
        }
    }

    private void showViewIdDialog() {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);
        View view = wB.a(getContext(), R.layout.property_popup_input_text);
        EditText input = view.findViewById(R.id.ed_input);
        input.setPrivateImeOptions("defaultInputmode=english;");
        input.setLines(1);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        _B validator = new _B(context, view.findViewById(R.id.ti_input), uq.b, uq.a(), jC.a(sc_id).a(projectFileBean), value);
        validator.a(value);
        dialog.a(view);
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (validator.b()) {
                setValue(input.getText().toString());
                if (valueChangeListener != null) valueChangeListener.a(key, value);
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public void a(String projectId, ProjectFileBean projectFileBean) {
        sc_id = projectId;
        this.projectFileBean = projectFileBean;
    }

    private void showNumberInputDialog() {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);
        View view = wB.a(getContext(), R.layout.property_popup_input_text);
        EditText input = view.findViewById(R.id.ed_input);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        input.setText(value);
        TB validator = new TB(context, view.findViewById(R.id.ti_input), 0,
                (key.equals("property_max") || key.equals("property_progress")) ? 0x7fffffff : 999);
        dialog.a(view);
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (validator.b()) {
                setValue(input.getText().toString());
                if (valueChangeListener != null) valueChangeListener.a(key, value);
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void showTranslationErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Erro");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK", null);
        AlertDialog errorDialog = builder.create();
        errorDialog.show();
    }

    private void showTextInputDialog(int minValue, int maxValue) {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);
        View view = wB.a(getContext(), R.layout.property_popup_input_text);
        EditText input = view.findViewById(R.id.ed_input);
        SB lengthValidator = new SB(context, view.findViewById(R.id.ti_input), minValue, maxValue);
        lengthValidator.a(value);
        dialog.a(view);
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (lengthValidator.b()) {
                try {
                    setValue(input.getText().toString());
                    if (valueChangeListener != null) valueChangeListener.a(key, value);
                    dialog.dismiss();
                } catch (Exception e) {
                    String errorMessage = "Erro ao salvar valor: " + e.getMessage();
                    showTranslationErrorDialog(errorMessage);
                }
            }
        });
        /*
        dialog.a(v ->{

            String translatedText;
            String text = input.getText().toString();
            translatedText = String.valueOf(new TranslateAPI("auto", "pt", text));
            input.setText(translatedText);
            Toast.makeText(context, "Conteúdo traduzido para o Português!", Toast.LENGTH_SHORT).show();
        });
        */
            String[] languageOptions = {"Inglês", "Espanhol", "Português"};

            // Cria o dialog para selecionar o idioma
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Selecione o idioma para tradução")
                    .setItems(languageOptions, (dialog1, which) -> {
                        // Obtém o idioma selecionado
                        String selectedLanguage = languageOptions[which];
                        String targetLanguageCode = "";
                        switch (selectedLanguage) {
                            case "Inglês":
                                targetLanguageCode = "en";
                                break;
                            case "Espanhol":
                                targetLanguageCode = "es";
                                break;
                            case "Português":
                                targetLanguageCode = "pt";
                                break;
                        }
                        // Obtém o texto de entrada
                        String text = input.getText().toString();

                        try {
                            TranslateAPI translator = new TranslateAPI("auto", targetLanguageCode, text);

                            translator.setTranslateListener(new TranslateAPI.TranslateListener() {
                                @Override
                                public void onSuccess(String translatedText) {
                                    //Log.d(TAG, "Translated text: " + translatedText);
                                    input.setText(translatedText);
                                }

                                @Override
                                public void onFailure(String errorText) {
                                    //Log.e(TAG, "Translation failed: " + errorText);
                                    showTranslationErrorDialog(errorText);
                                }
                            });
                            translator.execute();
                            //Toast.makeText(context, "Conteúdo traduzido para o " + selectedLanguage + "!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            String errorMessage = "Erro ao traduzir o texto: " + e.getMessage();
                            showTranslationErrorDialog(errorMessage);
                        }
                    });
        dialog.a(v ->{
            // Abre o dialog para selecionar o idioma
            builder.show();
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.configureDefaultButton("Code Editor", v -> {
            AsdAllEditor editor = new AsdAllEditor((Activity) this.getContext());
            editor.setCon(input.getText().toString());
            editor.show();
            editor.saveLis(logicEditor, null, editor);
            editor.cancelLis(logicEditor, editor);
            dialog.dismiss();
        });
        dialog.show();
    }

    /* // metodo original
    private String translate(String text) {
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlStr = "https://translate.google.com/?sl=auto&tl=pt&text=" + encodedText;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            String translated = result.substring(result.indexOf("<span title=\"") + 13, result.indexOf("\">", result.indexOf("<span title=\"") + 13));
            return translated;
        } catch (MalformedURLException e) {
            // Tratar o erro de URL mal formada
            String errorMessage = "A URL de conexão está mal formada: " + e.getMessage();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Erro");
            builder.setMessage(errorMessage);
            builder.setPositiveButton("OK", null);
            AlertDialog translatederror = builder.create();
            translatederror.show();
        } catch (IOException e) {
            // Tratar o erro de conexão
            String errorMessage;
            if (e instanceof UnknownHostException) {
                errorMessage = "Não foi possível conectar ao site de tradução: Host desconhecido";
            } else if (e instanceof ConnectException) {
                errorMessage = "Não foi possível conectar ao site de tradução: Conexão recusada";
            } else {
                errorMessage = "Ocorreu um erro ao acessar o site para traduzir o texto: " + e.getMessage();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Erro");
            builder.setMessage(errorMessage);
            builder.setPositiveButton("OK", null);
            AlertDialog translatederror = builder.create();
            translatederror.show();
        }
        return null;
    }
    */

    private void showNumberDecimalInputDialog(int minValue, int maxValue) {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);
        View view = wB.a(getContext(), R.layout.property_popup_input_text);
        EditText input = view.findViewById(R.id.ed_input);
        input.setInputType(minValue < 0 ?
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL
                : InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(value);
        OB validator = new OB(context, view.findViewById(R.id.ti_input), minValue, maxValue);
        dialog.a(view);
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (validator.b()) {
                setValue(input.getText().toString());
                if (valueChangeListener != null) valueChangeListener.a(key, value);
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }
}
