package fukie.sieunhanhav.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.crashlytics.android.answers.Answers;

import java.util.Random;

import fukie.sieunhanhav.service.FloatingSearchService;
import fukie.sieunhanhav.task.IniAsyncTask;
import fukie.sieunhanhav.R;
import fukie.sieunhanhav.utils.DatabaseReader;
import fukie.sieunhanhav.utils.ResultFormater;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    ListView lstSuggest;
    TextView txtIdioms;
    TextView txtMean;
    public static DatabaseReader dbVoca;
    EditText search;
    String[] listResult;
    static Context context;
    Button bttnClear;
    Button bttnHideKB;
    TextView txtResult;
    ResultFormater resultFormater;
    RelativeLayout layoutBottom;
    LinearLayout layoutIdioms;
    LinearLayout layoutBm;
    ToggleButton bttnBm;
    Button bttnSpeaker;
    Button bttnTranhanh;
    final String PREFS_NAME = "MyPrefsFile";
    final String[] SEARCH_HINT = {"search", "search", "search", "lienhe", "lichsu", "danhdau", "thuthuat", "tìm kiếm"};
    private static final int CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE = 100;

    private enum AppStart {
        FIRST_TIME, FIRST_TIME_VERSION, NORMAL
    }

    private static final String LAST_APP_VERSION = "last_app_version";
    Boolean typed = false;
    SharedPreferences settings;
    static ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    int currentVersionCode;
    boolean isShowingList = true;
    String searchValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers());
        context = getApplication().getApplicationContext();
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences(PREFS_NAME, 0);
        //final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);

        txtResult = (TextView) findViewById(R.id.textview_result);
        txtResult.setMovementMethod(new ScrollingMovementMethod());
        txtIdioms = (TextView) findViewById(R.id.textview_idioms);
        txtMean = (TextView) findViewById(R.id.textview_mean);
        search = (EditText) findViewById(R.id.search);
        search.getBackground().mutate().setColorFilter(ContextCompat.getColor(context, R.color.background_all), PorterDuff.Mode.SRC_ATOP);
        Random rd = new Random();

        search.setHint(SEARCH_HINT[rd.nextInt(7)]);
        search.setTypeface(null, Typeface.BOLD); //only text style(only bold)
        search.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        layoutBottom = (RelativeLayout) findViewById(R.id.layout_bottom);
        layoutBm = (LinearLayout) findViewById(R.id.layout_bm);
        bttnBm = (ToggleButton) findViewById(R.id.button_bm);
        bttnSpeaker = (Button) findViewById(R.id.button_speaker);
        layoutIdioms = (LinearLayout) findViewById(R.id.layout_idioms);
        lstSuggest = (ListView) findViewById(R.id.list_suggest);
        bttnClear = (Button) findViewById(R.id.button_clear);
        assert bttnClear != null;
        bttnClear.setVisibility(View.INVISIBLE);
        bttnHideKB = (Button) findViewById(R.id.button_hide_kb);
        bttnTranhanh = (Button) findViewById(R.id.button_tranhanh);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch (checkAppStart()) {
            case NORMAL:
                dbVoca = DatabaseReader.load(getApplication().getApplicationContext(), "voca", false);
                showIdioms();
                break;
            case FIRST_TIME_VERSION:
                progressDialog.setMessage("Cập nhật cơ sở dữ liệu sau khi cập nhật");
                progressDialog.show();
                search.setHint("search");
                //iniDatabase(this);
                if (!IniAsyncTask.isRunning) {
                    IniAsyncTask x = new IniAsyncTask() {
                        @Override
                        protected void onPostExecute(Void result) {
                            // settings.edit().putBoolean("my_first_time", false).apply();
                            sharedPreferences.edit().putInt(LAST_APP_VERSION, currentVersionCode).apply();
                            progressDialog.dismiss();
                            showIdioms();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            IniAsyncTask.isRunning = true;
                            dbVoca = DatabaseReader.load(getApplication().getApplicationContext(), "voca", true);
                            return null;
                        }
                    };
                    x.execute();
                    progressDialog.dismiss();
                    Intent intent = new Intent(this, IntroActivity.class);
                    startActivity(intent);
                }
                break;
            case FIRST_TIME:
                progressDialog.setMessage("Chuẩn bị dữ liệu cho lần chạy đầu tiên");
                progressDialog.show();
                search.setHint("search");
                //iniDatabase(this);
                if (!IniAsyncTask.isRunning) {
                    IniAsyncTask x = new IniAsyncTask() {
                        @Override
                        protected void onPostExecute(Void result) {
                            sharedPreferences.edit().putInt(LAST_APP_VERSION, currentVersionCode).apply();
                            progressDialog.dismiss();
                            showIdioms();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            IniAsyncTask.isRunning = true;
                            dbVoca = DatabaseReader.load(getApplication().getApplicationContext(), "voca", true);
                            return null;
                        }
                    };
                    x.execute();
                    progressDialog.dismiss();
                    Intent intent = new Intent(this, IntroActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String key = extras.getString("key");
            if (key != null && key.length() == 0) {
                if (extras.containsKey("value")) {
                    showResult(key, dbVoca.get(key));
                } else {
                    showListResult(dbVoca.findKeys(key));
                }
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        bttnHideKB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(MainActivity.this);
            }
        });

        bttnBm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bttnBm.startAnimation(animRotate);
                if (!bttnBm.isChecked()) {
                    resultFormater.deleteBmWord();
                } else {
                    resultFormater.bookmarkWord();
                }
            }
        });

        bttnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // bttnSpeaker.startAnimation(animRotate);
                resultFormater.pronunceWord();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!typed || !isShowingList) {
                    lstSuggest.setVisibility(View.VISIBLE);
                    layoutIdioms.setVisibility(View.GONE);
                    txtResult.setVisibility(View.GONE);
                    layoutBm.setVisibility(View.GONE);
                    bttnTranhanh.setVisibility(View.VISIBLE);
                    typed = true;
                    isShowingList = true;
                }
                String key = s.toString();

                if (s.length() > 0)
                    bttnClear.setVisibility(View.VISIBLE);
                else
                    bttnClear.setVisibility(View.INVISIBLE);

                if (key.length() > 1) {
                    listResult = dbVoca.findKeys(key);
                    if (listResult.length == 0) {
                        search.setTextColor(ContextCompat.getColor(context, R.color.red));
                    } else {
                        search.setTextColor(ContextCompat.getColor(context, R.color.black));
                        showListResult(listResult);
                    }
                } else {
                    lstSuggest.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bttnTranhanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTraNhanh();
            }
        });
        bttnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
                search.requestFocus();
                // formatEdittext(false, null);
                bttnHideKB.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.requestFocus();
                //formatEdittext(false, null);
                bttnHideKB.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        layoutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.requestFocus();
                //formatEdittext(false, null);
                bttnHideKB.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // formatEdittext(false, null);
                    bttnHideKB.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    //   formatEdittext(true, search.getText().toString());
                    bttnHideKB.setVisibility(View.GONE);
                    hideSoftKeyboard(MainActivity.this);
                }
            }
        });

        search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_ENTER) {
                    searchValue = search.getText().toString();
                    if (dbVoca.exists(searchValue)) {
                        switch (searchValue) {
                            case "lichsu":
                                showLichSu();
                                break;
                            case "danhdau":
                                showDanhDau();
                                break;
                            case "thuthuat":
                                showThuThuat();
                                break;
                            case "lienhe":
                                showLienHe();
                                break;
                            case "datlai":
                                showDatLai();
                                break;
                            case "tranhanh":
                                showTraNhanh();
                                break;
                            default:
                                //showResultActivity(searchValue, dbVoca.get(searchValue));
                                showResult(searchValue, dbVoca.get(searchValue));
                                break;
                        }
                        return true;
                    } else {
                        listResult = dbVoca.findKeys(searchValue);
                        if (listResult.length >= 2) {
                            showListResult(listResult);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

//    private void formatEdittext(boolean isDecorated, String key) {
//        if (isDecorated) {
//            bttnHideKB.setVisibility(View.GONE);
//            search.setTypeface(null, Typeface.BOLD); //only text style(only bold)
//            search.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
//            search.setText(key);
//        } else {
//            bttnHideKB.setVisibility(View.VISIBLE);
//            search.setTypeface(null, Typeface.NORMAL); //only text style(only bold)
//            search.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
//        }
//    }

    private void showResult(String key, String value) {
        Random rd = new Random();
        search.setHint(SEARCH_HINT[rd.nextInt(7)]);
        if (!checkDulicapeHistory(key)) {
            dbVoca.set("#h" + dbVoca.countKeys("#h") + ":" + key, value);
        }
        // formatEdittext(true, key);
        bttnHideKB.setVisibility(View.GONE);
        search.setText(key);
        if (isShowingList) {
            lstSuggest.setVisibility(View.GONE);
            txtResult.setVisibility(View.VISIBLE);
            layoutBm.setVisibility(View.VISIBLE);
            bttnTranhanh.setVisibility(View.GONE);
            isShowingList = false;
        }

        //   hideSoftKeyboard(this);
        resultFormater = new ResultFormater(key, value, context);
        txtResult.setText(Html.fromHtml(resultFormater.formatResult()));
        if (resultFormater.checkBookmarked()) {
            bttnBm.setChecked(true);
        } else {
            bttnBm.setChecked(false);
        }
        // search.clearFocus();
        txtResult.requestFocus();
    }

    private void showListResult(String[] strings) {
        if (!isShowingList) {
            lstSuggest.setVisibility(View.VISIBLE);
            txtResult.setVisibility(View.GONE);
            layoutBm.setVisibility(View.GONE);
            isShowingList = true;
        }
        strings = reverseStringArray(strings);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.list_result_item, R.id.textview_list_item, strings);
        lstSuggest.setAdapter(adapter);

        lstSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchValue = (String) lstSuggest.getItemAtPosition(position);
                switch (searchValue) {
                    case "lichsu":
                        showLichSu();
                        break;
                    case "danhdau":
                        showDanhDau();
                        break;
                    case "thuthuat":
                        showThuThuat();
                        break;
                    case "lienhe":
                        showLienHe();
                        break;
                    case "tranhanh":
                        showTraNhanh();
                        break;
                    case "datlai":
                        showDatLai();
                        break;
                    default:
                        showResult(searchValue, dbVoca.get(searchValue));
                        break;
                }
            }
        });
    }

    private boolean checkDulicapeHistory(String string) {
        String tmp = "#h" + (dbVoca.countKeys("#h") - 1) + ":" + string;
        return dbVoca.exists(tmp);
    }

    private void showDanhDau() {
        String[] list = dbVoca.findKeys("#b");
        if (list.length > 0) {
            String[] returnL = new String[list.length];
            int count = 0;
            if (list.length > 0) {
                for (String l : list) {
                    String[] tmp = l.split("\\^");
                    returnL[count++] = tmp[1];
                }
                showListResult(returnL);
            }
        } else {
            Toast toast = Toast.makeText(this, "Không có từ đã Đánh Dấu", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void showLichSu() {
        String[] list = dbVoca.findKeys("#h");
        if (list.length > 0) {
            String[] returnL = new String[list.length];
            if (list.length > 0) {
                for (String l : list) {
                    String[] tmp = l.split(":");
                    int pos = Integer.parseInt(tmp[0].substring(2));
                    returnL[pos] = tmp[1];
                }
                showListResult(reverseStringArray(returnL));
            }
        } else {
            Toast toast = Toast.makeText(this, "Không có từ đã tra", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void showLienHe() {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }


    private void showThuThuat() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    private void showDatLai() {
        new AlertDialog.Builder(this)
                .setTitle("Đặt Lại")
                .setMessage("Đặt lại toàn bộ Lịch Sử, Đánh Dấu?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //   progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Đặt lại toàn bộ dữ liệu");
                        progressDialog.show();

                        (new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected void onPostExecute(Void result) {
                                progressDialog.dismiss();
                                Toast toast = Toast.makeText(MainActivity.this, "Hoàn thành", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }

                            @Override
                            protected Void doInBackground(Void... params) {
                                String[] listH = dbVoca.findKeys("#h");
                                for (String mListH : listH) {
                                    dbVoca.delete(mListH);
                                }
                                String[] listB = dbVoca.findKeys("#b");
                                for (String mListB : listB) {
                                    dbVoca.delete(mListB);
                                }
                                return null;
                            }
                        }).execute();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void showTraNhanh() {
        // final Context context = getApplication().getApplicationContext();
        final boolean canShow = showChatHead(context);
        if (!canShow) {
            @SuppressLint("InlinedApi")
            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            startActivityForResult(intent, CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE);
        }
        this.finish();
    }

    @Override
    public void onDestroy() {
        dbVoca.close();
        super.onDestroy();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE) {
            final Context context = getApplication().getApplicationContext();
            final boolean canShow = showChatHead(context);
            if (!canShow) {
                Toast.makeText(context, "cant show", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("NewApi")
    private boolean showChatHead(Context context) {
        // API22以下かチェック
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            context.startService(new Intent(context, FloatingSearchService.class));
            return true;
        }

        // 他のアプリの上に表示できるかチェック
        if (Settings.canDrawOverlays(context)) {
            context.startService(new Intent(context, FloatingSearchService.class));
            return true;
        }

        return false;
    }

    private String[] reverseStringArray(String[] strings) {
        for (int i = 0; i < strings.length / 2; i++) {
            String temp = strings[i];
            strings[i] = strings[strings.length - i - 1];
            strings[strings.length - i - 1] = temp;
        }
        return strings;
    }

    private void showIdioms() {
        Random rand = new Random();
        int x = dbVoca.countKeys("#i");
        int n = rand.nextInt(x - 1);

        Typeface tf = Typeface.createFromAsset(getAssets(), "IntriqueScript_PersonalUse.ttf");
        txtIdioms.setTypeface(tf);

        String[] keys = dbVoca.findKeys("#i" + n);
        String[] key = keys[0].split("\\^");
        String quote = "<big><big><big><big><big><big>\"</big></big></big></big></big></big>";
        String displayIdioms = quote + "<big><big><big><big>" + key[1] + "</big></big></big></big>" + quote;

        String displayMean = dbVoca.get(keys[0]);

        txtIdioms.setText(Html.fromHtml(displayIdioms));
        txtMean.setText(Html.fromHtml(displayMean));
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public AppStart checkAppStart() {
        PackageInfo pInfo;

        AppStart appStart = AppStart.NORMAL;
        try {
            int lastVersionCode = sharedPreferences.getInt(LAST_APP_VERSION, -1);
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionCode = pInfo.versionCode;
            appStart = checkAppStart(currentVersionCode, lastVersionCode);

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "Unable to determine current app version from pacakge manager. Defenisvely assuming normal app start.", Toast.LENGTH_SHORT).show();
        }
        return appStart;
    }

    public AppStart checkAppStart(int currentVersionCode, int lastVersionCode) {
        if (lastVersionCode == -1) {
            return AppStart.FIRST_TIME;
        } else if (lastVersionCode < currentVersionCode) {
            return AppStart.FIRST_TIME_VERSION;
        } else {
            return AppStart.NORMAL;
        }
    }

}

