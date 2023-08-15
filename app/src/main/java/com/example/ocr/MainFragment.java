package com.example.ocr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Locale;
public class MainFragment extends Fragment {
    private static final int PICK_IMAGE = 123;
    Button chooseBtn;
    Button extractBtn;
    TextToSpeech textToSpeech;
    TextView extractedText;
    public Bitmap textImage;
    TextRecognizer recognizer;
    InputImage inputImage;
    ImageView imageView;
    public MainFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
         imageView = view.findViewById(R.id.imageView);
        chooseBtn = view.findViewById(R.id.selectImageBtn);
        extractBtn = view.findViewById(R.id.extractImgBtn);
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/");
        Intent selectedIntent = Intent.createChooser(getIntent, "Select Image");
        selectedIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(selectedIntent, PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                byte[] byteArray = new byte[0];
                String filePath = null;

                try {
                    inputImage = InputImage.fromFilePath(requireContext(), data.getData());
                    Bitmap resultURI = inputImage.getBitmapInternal();

                    //assign the loaded image to the imageview
                    imageView.setImageBitmap(resultURI);

                    Glide.with(requireContext())
                            .load(resultURI)
                            .into(imageView);

                    extractBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //process the image
                            Task<Text> result = recognizer.process(inputImage)
                                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                                        @Override
                                        public void onSuccess(Text text) {
                                            ProcessTextBlock(text);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void ProcessTextBlock(Text text) {

        StringBuilder extractedTextBuilder = new StringBuilder();

        String resultText = text.getText();
        for(Text.TextBlock block : text.getTextBlocks()) {
            String blockText = block.getText();
            extractedTextBuilder.append("\n");

            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();

            for(Text.Line line : block.getLines()) {
                String lineText = line.getText();

                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();

                for(Text.Element element : line.getElements()) {
                    extractedTextBuilder.append(" ");
                    String elementText = element.getText();
                    extractedTextBuilder.append(elementText);

                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
                extractedTextBuilder.append("\n");
            }
        }

        // Pass the extracted text to PdfFragment
        PdfFragment pdfFragment = new PdfFragment();
        Bundle bundle = new Bundle();
        bundle.putString("EXTRACTED_TEXT", extractedTextBuilder.toString());
        pdfFragment.setArguments(bundle);

        // Replace the MainFragment with PdfFragment
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.navHostFragment, pdfFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}