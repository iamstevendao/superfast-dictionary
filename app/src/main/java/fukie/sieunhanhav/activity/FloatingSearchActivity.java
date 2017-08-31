package fukie.sieunhanhav.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import fukie.sieunhanhav.R;
import fukie.sieunhanhav.utils.DatabaseReader;
import fukie.sieunhanhav.utils.ResultFormater;

public class FloatingSearchActivity extends AppCompatActivity {
    EditText search;
    TextView txtResult;
    ListView lstSuggest;
    String[] listSuggest;
    Context context;
    Boolean showList;
    DatabaseReader dbVoca;
    Button bttnToMain;
    public static boolean active = false;
    ResultFormater resultFormater;
    Button bttnSpeakerFloat;
    ToggleButton bttnBmFloat;
    RelativeLayout layoutResultFloat;
    Button bttnClearFloat;
    static FloatingSearchActivity floatingSearchActivity;
    String searchValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_search);
        active = true;
        floatingSearchActivity = this;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = 0;
        params.y = 0;
        params.width = getResources().getDimensionPixelSize(R.dimen.mini_window_width);
        params.height = getResources().getDimensionPixelSize(R.dimen.mini_window_height);
        Window activityWindow = getWindow();
        activityWindow.setAttributes(params);
        activityWindow.setDimAmount(0);
        context = getApplication().getApplicationContext();
        search = (EditText) findViewById(R.id.search_float);
        txtResult = (TextView) findViewById(R.id.result_float);
        txtResult.setMovementMethod(new ScrollingMovementMethod());
        bttnToMain = (Button) findViewById(R.id.button_to_main);
        lstSuggest = (ListView) findViewById(R.id.suggest_float);
        bttnSpeakerFloat = (Button) findViewById(R.id.button_speaker_float);
        bttnBmFloat = (ToggleButton) findViewById(R.id.button_bm_float);
        layoutResultFloat = (RelativeLayout) findViewById(R.id.layout_result_float);
        bttnClearFloat = (Button) findViewById(R.id.button_clear_float);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        dbVoca = DatabaseReader.load(context, "voca", false);
        showList = true;
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!showList) {
                        lstSuggest.setVisibility(View.VISIBLE);
                        layoutResultFloat.setVisibility(View.GONE);
                        showList = true;
                    }
                    String key = s.toString();
                    if (s.length() > 0)
                        bttnClearFloat.setVisibility(View.VISIBLE);
                    else
                        bttnClearFloat.setVisibility(View.INVISIBLE);
                    if (key.length() > 1) {
                        listSuggest = dbVoca.findKeys(key);
                        if (listSuggest.length == 0) {
                            search.setTextColor(ContextCompat.getColor(context, R.color.red));
                        } else {
                            search.setTextColor(ContextCompat.getColor(context, R.color.black));
                            showListSuggest(listSuggest);
                        }
                    } else {
                        lstSuggest.setAdapter(null);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bttnClearFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });
        bttnToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("key", searchValue);
                intent.putExtra("value", dbVoca.get(searchValue));
                startActivity(intent);
                finish();
            }
        });
        bttnBmFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bttnBmFloat.isChecked()) {
                    resultFormater.deleteBmWord();
                } else {
                    resultFormater.bookmarkWord();
                }
            }
        });
        bttnSpeakerFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultFormater.pronunceWord();
            }
        });

//        search.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN
//                        && keyCode == KeyEvent.KEYCODE_ENTER) {
//                    String searchValue = search.getText().toString();
//                    if (dbVoca.exists(searchValue)) {
//                        showResult(searchValue, dbVoca.get(searchValue));
//                    } else {
//                        String[] listResult = dbVoca.findKeys(searchValue);
//                        if (listResult.length >= 2) {
//                            showListSuggest(listResult);
//                        }
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search.requestFocus();
                    searchValue = search.getText().toString();
                    if (dbVoca.exists(searchValue)) {
                        showResult(searchValue, dbVoca.get(searchValue));
                    } else {
                        String[] listResult = dbVoca.findKeys(searchValue);
                        if (listResult.length >= 2) {
                            showListSuggest(listResult);
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }

        });
    }

    private void showListSuggest(String[] strings) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.list_result_item, R.id.textview_list_item, strings);
        lstSuggest.setAdapter(adapter);

        lstSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchValue = (String) lstSuggest.getItemAtPosition(position);
                showResult(searchValue, dbVoca.get(searchValue));
            }
        });
    }

    private void showResult(String key, String value) {
        if(showList) {
            layoutResultFloat.setVisibility(View.VISIBLE);
            lstSuggest.setVisibility(View.GONE);
            showList = false;
        }
        resultFormater = new ResultFormater(key, value, context);
        if (resultFormater.checkBookmarked()) {
            bttnBmFloat.setChecked(true);
        } else {
            bttnBmFloat.setChecked(false);
        }
        txtResult.setText(Html.fromHtml(resultFormater.formatMinimalResult()));
    }

    @Override
    public void onDestroy() {
        // dbVoca.close();
        active = false;
        super.onDestroy();
    }

    public static FloatingSearchActivity getInstance() {
        return floatingSearchActivity;
    }
}
