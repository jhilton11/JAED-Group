package com.appify.jaedgroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import androidx.annotation.Nullable;

public class OnBoardingScreenActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .title("Slide 1")
                .description("Description of Slide 1")
                .build()
        );

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorAccent)
                .title("")
                .description("Description of Slide 2")
                .build()
        );

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimaryDark)
                .title("")
                .description("")
                .build(),
        new MessageButtonBehaviour(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(OnBoardingScreenActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, "Finish"));
    }

    @Override
    public void onFinish() {
        super.onFinish();
    }
}
