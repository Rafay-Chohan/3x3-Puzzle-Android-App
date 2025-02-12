package com.example.SMD;

import android.widget.Toast;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ImageView[] imageViews= new ImageView[10];
    private Bitmap selectedBitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button selectImageButton,resetImageButton,shuffleImageButton;
    private int[] pos={0,1,2,3,4,5,6,7,8,9};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        init();
        shuffleImageButton.setOnClickListener(v->shuffle());
        resetImageButton.setOnClickListener(v->reset());
        selectImageButton.setOnClickListener(v -> pickImageFromGallery());
        // Set OnClickListener for each ImageView
        for (int i = 1; i < imageViews.length-1; i++) {
            final int index = i; // Capture index for use inside listener
            imageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Clicked", index + " image");
                    if (pos[index]+3==pos[9]||pos[index]-3==pos[9]||pos[index]+1==pos[9]||pos[index]-1==pos[9]){
                        swapPosition(pos,index);
                    }
                }
            });
        }
    }
    private void init(){
        imageViews[1] = findViewById(R.id.imageView1);
        imageViews[2] = findViewById(R.id.imageView2);
        imageViews[3] = findViewById(R.id.imageView3);
        imageViews[4] = findViewById(R.id.imageView4);
        imageViews[5] = findViewById(R.id.imageView5);
        imageViews[6] = findViewById(R.id.imageView6);
        imageViews[7] = findViewById(R.id.imageView7);
        imageViews[8] = findViewById(R.id.imageView8);
        imageViews[9]= findViewById(R.id.blank);
        selectImageButton = findViewById(R.id.btnLoadImage);
        resetImageButton = findViewById(R.id.btnReset);
        shuffleImageButton = findViewById(R.id.btnShuffle);
        loadInitialImageFromAssets();
    }
    private void reset(){
        loadInitialImageFromAssets();
    }
    private void shuffle(){
        Random random = new Random();

        for(int i=0;i<6;i++)
        {
            int randomNumber = random.nextInt(8)+1;
            swapPosition(pos,randomNumber);
        }
    }
    private boolean checkWinCon()
    {
        boolean flag=true;
        for(int i=0;i<pos.length;i++){
            if(pos[i]!=i)
                flag=false;
        }
     return flag;
    }
    private void swapPosition(int[] arr,int x) {
        int index1=x;
        int index2=9;
        Log.d("TAG", "Swapped ImageViews at positions: " + index1 + " and " + index2);
        ImageView view1 = imageViews[index1];
        ImageView view2 = imageViews[index2];

        ViewGroup parent = (ViewGroup) view1.getParent();
        if (parent == null || parent != view2.getParent()) {
            ViewGroup parent1 = (ViewGroup) view1.getParent();
            ViewGroup parent2 = (ViewGroup) view2.getParent();
            // Get positions in their respective parents
            int indexInParent1 = parent1.indexOfChild(view1);
            int indexInParent2 = parent2.indexOfChild(view2);

            // Remove from parents
            parent1.removeView(view1);
            parent2.removeView(view2);

            // Swap and add back to the opposite parents at the same index
            parent1.addView(view2, indexInParent1);
            parent2.addView(view1, indexInParent2);
        }
        else {
            int indexInParent1 = parent.indexOfChild(view1);
            int indexInParent2 = parent.indexOfChild(view2);

            // Swap positions in LinearLayout
            if (indexInParent1 > indexInParent2) {
                parent.removeView(view1);
                parent.removeView(view2);
                parent.addView(view1, indexInParent2);
                parent.addView(view2, indexInParent1);
            } else {
                parent.removeView(view2);
                parent.removeView(view1);
                parent.addView(view2, indexInParent1);
                parent.addView(view1, indexInParent2);
            }
        }


        int b=arr[x];
        arr[x]=arr[9];
        arr[9]=b;
        if(checkWinCon())
            Toast.makeText(this,"Congratulations for Completing the Puzzle",Toast.LENGTH_LONG).show();
    }
    private void loadInitialImageFromAssets() {
        selectedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image); // Change to your drawable resource name
        selectedBitmap = makeSquare(selectedBitmap);
        splitImage(selectedBitmap);
    }
    //Image Swapping Functionality
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                selectedBitmap = makeSquare(selectedBitmap);
                splitImage(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap makeSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newSize = Math.max(width, height);
        Bitmap squaredBitmap = Bitmap.createBitmap(newSize, newSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(squaredBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, (newSize - width) / 2f, (newSize - height) / 2f, new Paint());
        return squaredBitmap;
    }

    private void splitImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int blockSize = width / 3;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Bitmap subImage = Bitmap.createBitmap(bitmap, col * blockSize, row * blockSize, blockSize, blockSize);
                imageViews[(row * 3 + col)+1].setImageBitmap(subImage);
            }
        }
        imageViews[9].setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.blank));
    }
}