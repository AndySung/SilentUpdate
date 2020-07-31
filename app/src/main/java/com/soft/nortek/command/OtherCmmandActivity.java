package com.soft.nortek.command;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.soft.nortek.custom.ShellUtils;
import com.soft.nortek.silentupdate.R;


public class OtherCmmandActivity extends AppCompatActivity implements View.OnClickListener{
    private Button getpropBtn;
    private TextView version_txt;
    String GETPROP = "getprop SP3_Hw_Info";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_command_layout);
        /* 显示App icon左侧的back键 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        onClick();
    }

    private void onClick() {
        getpropBtn.setOnClickListener(this);
    }

    private void initView() {
        getpropBtn = findViewById(R.id.getprop);
        version_txt = findViewById(R.id.version_txt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getprop:
                ShellUtils.execCommand(GETPROP,false);
                version_txt.setText(ShellUtils.execCommand(GETPROP,false).successMsg);
                break;
        }
    }
}
