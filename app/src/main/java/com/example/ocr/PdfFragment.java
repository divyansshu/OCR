package com.example.ocr;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.ClipboardManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
public class PdfFragment extends Fragment {
    TextView textView;
    Button shareBTN;
    public PdfFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pdf, container, false);

        //retrieve the extracted text from the bundle arguments
        Bundle bundle = getArguments();
        assert bundle != null;
        String extractedText = bundle.getString("EXTRACTED_TEXT");

        //display the extracted text in textView
         textView = view.findViewById(R.id.displayText);
        textView.setText(extractedText);
        shareBTN = view.findViewById(R.id.shareBtn);
        textView.setTextIsSelectable(true);

       return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String selectedText = textView.getText().toString().substring(textView.getSelectionStart(), textView.getSelectionEnd());
                ClipboardManager clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", selectedText);
                clipboardManager.setPrimaryClip(clipData);
//                Toast.makeText(getContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        shareBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareExtractedText();
            }
        });
    }
    private void shareExtractedText() {
        //retrieve the extracted text from the textView
        String extractedText = textView.getText().toString();

        // intent to share extracted text
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, extractedText);

        //start the sharing activity
        startActivity(Intent.createChooser(shareIntent, "Share Extracted Text"));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_back) {
            //navigate back to the previous fragment
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




