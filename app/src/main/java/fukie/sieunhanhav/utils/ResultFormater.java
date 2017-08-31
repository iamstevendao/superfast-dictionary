package fukie.sieunhanhav.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import fukie.sieunhanhav.activity.MainActivity;

/**
 * Created by Fukie on 31/03/2016.
 */
public class ResultFormater {
    private String searchKey;
    private String result;
    private TextToSpeech t1;

    public ResultFormater(String key, String value, Context context) {
        this.searchKey = key;
        this.result = value;
        t1 = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });
    }

    public String formatResult() {
        return toViewFormat(spilitResult(result));
    }

    public String formatMinimalResult() {
        return toMinimalFormat(spilitResult(result));
    }

    private String[] spilitResult(String value) {
        String[] at = value.split("(?=@)");
        ArrayList<String> al = new ArrayList<>();
        for (String tmpAt : at) {
            String[] hyphen = tmpAt.split("(?=-)");
            for (String tmpHyphen : hyphen) {
                String[] slash = tmpHyphen.split("(?=/)");
                for (String tmpSlash : slash) {
                    String[] equals = tmpSlash.split("(?==)");
                    for (String tmpEquals : equals) {
                        String[] plusSign = tmpEquals.split("(?=\\+)");
                        for (String tmpPlusSign : plusSign) {
                            String[] asterisk = tmpPlusSign.split("(?=\\*)");
                            for (String tmpAsterisk : asterisk) {
                                int n;
                                int pos = 0;
                                String x = tmpAsterisk;
                                while ((n = x.indexOf('!')) != -1) {
                                    if (tmpAsterisk.length() > n + pos + 1) {
                                        if (Character.isLetter(tmpAsterisk.charAt(n + pos + 1)) ||
                                                tmpAsterisk.charAt(n + pos + 1) == '[') {
                                            tmpAsterisk = tmpAsterisk.substring(0, n + pos) + '^' + tmpAsterisk.substring(n + pos + 1);
                                        }
                                    }
                                    x = x.substring(n + 1);
                                    pos += n + 1;
                                }
                                String[] caret = tmpAsterisk.split("(?=\\^)");
                                Collections.addAll(al, caret);
//                                for (String tmpCaret : caret) {
//                                    al.add(tmpCaret);
//                                }
                            }
                        }
                    }
                }
            }
        }
        return al.toArray(new String[0]);
    }

    private String toViewFormat(String[] strings) {
        String returnString = "";
        for (String string : strings) {
            if (string.length() > 0) {
                switch (string.charAt(0)) {
                    case '@':
                        if (searchKey.equals(string.substring(1).trim())) {
                            returnString += "<b>" + string.substring(1).toUpperCase() + "</b>";
                        } else {
                            returnString += "<br/><br/><b>" + string.substring(1).toUpperCase() + "</b>";
                        }
                        break;
                    case '-':
                        returnString += "<br/>" + string;
                        break;
                    case '/':
                        if (string.length() > 1) {
                            returnString += "<br/><i>" + string + "/</i>";
                        }
                        break;
                    case '*':
                        returnString += "<br/><b><font color='#b71c1c'>" + string.substring(2) + "</font></b>";
                        break;
                    case '^':
                        returnString += "<br/><font color='#3f51b5'><b>" + string.substring(1) + "</b></font>";
                        break;
                    case '=':
                        if (Character.isLetter(string.charAt(1))) {
                            returnString += "<br/><font color='#607d8b'><i>" + string.substring(1) + "</i></font>";
                        } else {
                            returnString += string;
                        }
                        break;
                    case '+':
                        returnString += "<br/><font color='#607d8b'></i>->" + string.substring(1) + "</i></font>";
                        break;
                    default:
                        returnString += string;
                        break;
                }
            }
        }
        return returnString;
    }

    private String toMinimalFormat(String[] strings){
        String returnString = "";
        for (String string : strings) {
            if(string.length() > 0){
                switch (string.charAt(0)) {
                    case '-':
                        returnString += "<br/>" + string.substring(1);
                        break;
                }
            }
        }
        return returnString;
    }

    public boolean checkBookmarked() {
        return MainActivity.dbVoca.exists("#b^" + searchKey);
    }

    public void bookmarkWord() {
        MainActivity.dbVoca.set("#b^" + searchKey, result);
    }

    public void deleteBmWord() {
        MainActivity.dbVoca.delete("#b^" + searchKey);
    }

    public void pronunceWord() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(searchKey);
        } else {
            ttsUnder20(searchKey);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}
