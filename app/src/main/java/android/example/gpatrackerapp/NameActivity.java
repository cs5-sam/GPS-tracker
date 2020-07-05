package android.example.gpatrackerapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class NameActivity extends AppCompatActivity {

    String email,password;

    EditText nameReg;
    Button nextBtn;

    CircleImageView circleImageView;

    Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        nameReg = findViewById(R.id.name_edit);
        nextBtn = findViewById(R.id.confirm_name_btn);
        circleImageView = findViewById(R.id.circle_image_view);

        Intent i = getIntent();
        if(i!=null){
            email = i.getStringExtra("email");
            password = i.getStringExtra("password");
        }

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCode();
            }
        });
    }

    public void generateCode(){
        Date myDate = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());

        String date = format1.format(myDate);
        Random  r = new Random();
        int n = 100000 + r.nextInt(900000);
        String code = String.valueOf(n);

        if(resultUri != null){
            Intent newInt = new Intent(NameActivity.this,InviteCodeActivity.class);

            newInt.putExtra("name",nameReg.getText().toString());
            newInt.putExtra("email",email);
            newInt.putExtra("password",password);
            newInt.putExtra("date",date);
            newInt.putExtra("isSharing","false");
            newInt.putExtra("code",code);
            newInt.putExtra("imageUri",resultUri);

            startActivity(newInt);
            finish();
        }
        else{
            Toast.makeText(this, "Please choose an image!", Toast.LENGTH_SHORT).show();
        }

    }

    public void selectImage(){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i,12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12 && resultCode == RESULT_OK && data != null) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                circleImageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
