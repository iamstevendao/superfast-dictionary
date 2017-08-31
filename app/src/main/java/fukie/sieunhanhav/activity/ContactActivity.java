package fukie.sieunhanhav.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;

import fukie.sieunhanhav.R;

public class ContactActivity extends AppCompatActivity {
    LinearLayout layoutMail;
    LikeView likeView;
    ImageView imgFanpage;
    LinearLayout layoutFanpage;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_contact);
        context = getApplication().getApplicationContext();
        likeView = (LikeView) findViewById(R.id.likeView);
        likeView.setObjectIdAndType("909251402526130", LikeView.ObjectType.PAGE);
        likeView.setAuxiliaryViewPosition(LikeView.AuxiliaryViewPosition.INLINE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        TextView txtThongtin = (TextView) findViewById(R.id.textview_thongtin);
        txtThongtin.setText(Html.fromHtml(getThongtin()));

        imgFanpage = (ImageView) findViewById(R.id.img_fanpage);
        layoutFanpage = (LinearLayout) findViewById(R.id.layout_fanpage);
        TextView txtCamon = (TextView) findViewById(R.id.textview_camon);
        String camon = "<b>Cám ơn bạn đã sử dụng từ điển Siêu Nhanh!</b>";
        txtCamon.setText(Html.fromHtml(camon));
        Button bttnMail = (Button) findViewById(R.id.button_mail);
        Button bttnRate = (Button) findViewById(R.id.button_rate);

        imgFanpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String packageName = context.getApplicationContext().getPackageName();
                newFacebookIntent(packageName, context.getPackageManager(), "https://www.facebook.com/tudiensieunhanh");
            }
        });
        layoutFanpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String packageName = context.getApplicationContext().getPackageName();
                newFacebookIntent(packageName, context.getPackageManager(), "https://www.facebook.com/tudiensieunhanh");
            }
        });
        bttnMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:sieunhanhav@gmail.com")); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ContactActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bttnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        LinearLayout layout_mail = (LinearLayout) findViewById(R.id.layout_mail);
        LinearLayout layout_rate = (LinearLayout) findViewById(R.id.layout_rate);
        layout_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:sieunhanhav@gmail.com")); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ContactActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        layout_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
    }

    private String getThongtin() {
        String string = "<b>Đào Phú Kiên</b>, 1995<br/>Đại học Bách Khoa Hà Nội";
        return string;
    }

    public void  newFacebookIntent(String packageName, PackageManager pm, String url) {
        try {
            int versionCode = pm.getPackageInfo(packageName, 0).versionCode;
            if (versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));;
            } else {
                // open the Facebook app using the old method (fb://profile/id or fb://page/id)
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Facebook is not installed. Open the browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }
}
