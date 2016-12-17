package com.nice295.huegallery;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.util.HashMap;

import io.paperdb.Book;
import io.paperdb.Paper;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Paper.init(this);
        Boolean skipIntro = Paper.book().read("skipIntro", false);
        if (skipIntro == true) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Paper.book().write("skipIntro", true);
        }

        addSlide(AppIntroFragment.newInstance(getString(R.string.intro1_title), getString(R.string.intro1_desc),
                R.drawable.ic_launcher,
         Color.parseColor("#ffffff"),Color.parseColor("#3F51B5"), Color.parseColor("#000000")));

        setSkipText(getResources().getString(R.string.skip));
        setColorSkipButton(Color.parseColor("#000000"));

        setDoneText(getResources().getString(R.string.done));
        setColorDoneText(Color.parseColor("#000000"));

        setNextArrowColor(Color.parseColor("#000000"));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
