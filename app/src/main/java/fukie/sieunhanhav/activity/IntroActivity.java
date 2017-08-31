package fukie.sieunhanhav.activity;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import fukie.sieunhanhav.R;

/**
 * Created by Fukie on 18/03/2016.
 */
public class IntroActivity extends AppIntro {
    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance("Xin chào", "với đầy đủ loại từ, ví dụ, chức năng, chuyên ngành", R.drawable.fragment1, R.color.red));
        addSlide(AppIntroFragment.newInstance("Idioms", "Các Thành ngữ tiếng Anh khi mở App", R.drawable.fragment2, R.color.black));
        addSlide(AppIntroFragment.newInstance("Lịch sử tra cứu", "Bằng cách tra từ \"lichsu\"", R.drawable.lichsu, R.color.blue));
        addSlide(AppIntroFragment.newInstance("\"danhdau\"", "Danh sách các từ đã Đánh dấu", R.drawable.bookmark, R.color.green));
        addSlide(AppIntroFragment.newInstance("\"thuthuat\"", "Thủ thuật, hướng dẫn", R.drawable.thuthuat, R.color.black));
        addSlide(AppIntroFragment.newInstance("\"datlai\"", "Đặt lại, reset toàn bộ dữ liệu", R.drawable.reset, R.color.white));
        addSlide(AppIntroFragment.newInstance("\"lienhe\"", "Share, like, comment, rate hoặc liên hệ tác giả!", R.drawable.lienhe, R.color.red));
        // OPTIONAL METHODS
        // Override bar/separator color.

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        setVibrate(false);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.

    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }
}
