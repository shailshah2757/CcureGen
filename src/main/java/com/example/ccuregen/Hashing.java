package com.example.ccuregen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import org.apache.commons.codec.digest.DigestUtils;
public class Hashing extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;

    TextView tv;
    Intent myFileIntent;
    Button btn;

    Button btnGenerateTextHash;

    EditText editText;
    TextView generatedText;

    private static final int PICKFILE_RESULT_CODE = 1;
    public static String md5Final = "";
    public static String sha1Final = "";
    public static String sha256Final = "";
    public static String sha512Final = "";
    public static String sha384Final = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashing);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable
                (getResources().getColor(R.color.action_bar_blue)));
        getSupportActionBar().setTitle("Hashing");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        generatedText = findViewById(R.id.generatedhashText);
        editText = findViewById(R.id.etTextHash);
        btnGenerateTextHash = findViewById(R.id.btnGenerateHash);
        tv = findViewById(R.id.hashtext);
        btn = findViewById(R.id.btnSelectFile);
        swipeToRefresh();
    }

    private void swipeToRefresh() {
        swipeContainer = findViewById(R.id.swipe_container);

        swipeContainer.setOnRefreshListener(() -> {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
            swipeContainer.setRefreshing(false);
        });

        swipeContainer.setColorSchemeResources( android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    protected void onStart() {
        super.onStart();

        btn.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent , 1);
        });
        btnGenerateTextHash.setOnClickListener(v->{
            try {
                String inputString = editText.getText().toString();
                System.out.println("Input String: " + inputString);

                String md5Hash = HashGeneratorUtils.generateMD5(inputString);
                System.out.println("MD5 Hash: " + md5Hash);
                String sha1Hash = HashGeneratorUtils.generateSHA1(inputString);
                System.out.println("SHA-1 Hash: " + sha1Hash);

                String sha256Hash = HashGeneratorUtils.generateSHA256(inputString);
                System.out.println("SHA-256 Hash: " + sha256Hash);
                generatedText.setText( "\nMD5 : "+md5Hash+"\n\nSHA-1"+sha1Hash+"\n\nSHA-256"+sha256Hash);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String filePath = selectedImage.getPath();
                //System.out.println(selectedImage.toString());
                String uripath = FilePath.getPath(getApplicationContext(), selectedImage);
                //final String[] split = uripath.split(":");
                //File file = new File(uripath);
                generatehash(uripath);
            }
        } catch (Exception ex) {
            Toast.makeText(Hashing.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
            System.out.println(ex.toString());
        }
;    }
    public void generatehash(String path){
        try
        {
            //Gets the file from the file chooser.
            File finalFile = new File(path);
            InputStream is = Files.newInputStream(finalFile.toPath());
            InputStream is2 = Files.newInputStream(finalFile.toPath());
            InputStream is3 = Files.newInputStream(finalFile.toPath());
            InputStream is4 = Files.newInputStream(finalFile.toPath());
            InputStream is5 = Files.newInputStream(finalFile.toPath());
            InputStream is6 = Files.newInputStream(finalFile.toPath());

            //Generate the checksum with the given file.
            md5Final = DigestUtils.md5Hex(is);
            sha1Final = DigestUtils.shaHex(is2);
            sha256Final = DigestUtils.sha256Hex(is3);
            sha512Final = DigestUtils.sha512Hex(is4);
            sha384Final = DigestUtils.sha384Hex(is5);

            Log.d("-------------->","No Error");
            Log.d("---------------->","MD5:" + md5Final + "\nSHA1: " + sha1Final + "\nSHA-256: " + sha256Final + "\nSHA-384: " + sha384Final + "\nsha512: " + sha512Final);
            //Puts the final output into the text box.
            tv.setText("\nMD5:" + md5Final + "\n\nSHA1: " + sha1Final + "\n\nSHA-256: " + sha256Final + "\n\nSHA-384: " + sha384Final + "\n\nsha512: " + sha512Final);

            tv.setPadding(0,0,0,100);
        }
        catch(IOException e)
        {
            Log.d("-------------->","Error");
            e.printStackTrace();
        }
    }
    private void calculateHash(String filePath) {
        File file = new File(filePath);
        try {
            String md5Hash = getMd5OfFile(filePath);
            System.out.println("MD5 hash of file " + file.getName() + ": " + md5Hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getMd5OfFile(String filePath)
    {
        String returnVal = "";
        try
        {
            InputStream   input   = new FileInputStream(filePath);
            byte[]        buffer  = new byte[1024];
            MessageDigest md5Hash = MessageDigest.getInstance("MD5");
            int           numRead = 0;
            while (numRead != -1)
            {
                numRead = input.read(buffer);
                if (numRead > 0)
                {
                    md5Hash.update(buffer, 0, numRead);
                }
            }
            input.close();

            byte [] md5Bytes = md5Hash.digest();
            for (int i=0; i < md5Bytes.length; i++)
            {
                returnVal += Integer.toString( ( md5Bytes[i] & 0xff ) + 0x100, 16).substring( 1 );
            }
        }
        catch(Throwable t) {t.printStackTrace();}
        return returnVal.toUpperCase();
    }


    public static String calculateHash(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] bytesBuffer = new byte[1024];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }
            byte[] hashedBytes = digest.digest();

            return convertByteArrayToHexString(hashedBytes);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
}