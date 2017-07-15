package me.esca.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import me.esca.R;
import me.esca.model.Image;
import me.esca.model.Recipe;
import me.esca.services.escaWS.recipes.AddNewRecipeService;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Me on 18/06/2017.
 */

public class CookFragment extends Fragment implements ServiceConnection {

    private EditText recipeTitleEditText;
    private EditText recipeIngredientsEditText;
    private EditText recipeInstructionsEditText;
    private EditText recipePreparationTimeEditText;
    private EditText recipePreparationCostEditText;
    private Button addRecipeButton;
    private Recipe recipeToBeAdded;
    private AddNewRecipeService addNewRecipeService;
    private DataUpdateReceiver dataUpdateReceiver;
    private SeekBar difficultyRatingSeekBar;
    private TextView difficultyTextView;
    private int difficultyRating;
    private Button addImageButton;
    private int RESULT_LOAD_IMG = 1;
    private ImageView recipeImageView;
    private Uri imageUri;
    private Image imageToBeAdded;
    private static final int REQUEST_EXTERNAL_STORAGE = 17;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private String messageBody = "";


    @Override
    public void onResume() {
        super.onResume();
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter("ServiceIsDone");
        getActivity().registerReceiver(dataUpdateReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null) getActivity().unregisterReceiver(dataUpdateReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cook_fragment, container, false);
        recipeTitleEditText = (EditText)view.findViewById(R.id.title_edit_text);
        recipeIngredientsEditText = (EditText)view.findViewById(R.id.ingredients_edit_text);
        recipeInstructionsEditText = (EditText)view.findViewById(R.id.instructions_edit_text);
        recipePreparationTimeEditText = (EditText)view.findViewById(R.id.prep_time_edit_text);
        recipePreparationCostEditText = (EditText)view.findViewById(R.id.prep_cost_edit_text);
//        recipeDifficultyRatingSpinner = (Spinner)view.findViewById(R.id.difficulty_rating_spinner);
        difficultyRatingSeekBar = (SeekBar) view.findViewById(R.id.difficultyRatingSeekBar);
        difficultyTextView = (TextView)view.findViewById(R.id.difficultyTextView);
        difficultyTextView.setText(String.valueOf(difficultyRatingSeekBar.getProgress()));
        difficultyRating = difficultyRatingSeekBar.getProgress();
        if(getActivity().getActionBar() != null )getActivity().getActionBar().hide();
        recipeImageView = (ImageView) view.findViewById(R.id.recipeImageView);
        difficultyRatingSeekBar.setOnSeekBarChangeListener(

                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progressValue, boolean fromUser) {
                        progress = progressValue;
                        difficultyTextView.setText(String.valueOf(progressValue));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        difficultyRating = progress;
                    }
                });

        addRecipeButton = (Button) view.findViewById(R.id.add_recipe_button);
        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataValidation()){
                    recipeToBeAdded = new Recipe(recipeTitleEditText.getText().toString().trim(),
                            difficultyRating,
                            Integer.parseInt(recipePreparationTimeEditText.getText().toString().trim()),
                            Double.valueOf(recipePreparationCostEditText.getText().toString().trim()),
                            recipeIngredientsEditText.getText().toString().trim(),
                            recipeInstructionsEditText.getText().toString().trim(), null, null, null, null);
                    imageToBeAdded = new Image(null, imageUri.getPath(), imageUri.getPath(),
                            null, null, true, null, recipeToBeAdded);
                    addNewRecipeService = new AddNewRecipeService();
                    Intent service = new Intent(getActivity().getApplicationContext(), AddNewRecipeService.class);
                    service.putExtra("recipeToBeAdded", recipeToBeAdded);
                    service.putExtra("imageToBeAdded", imageToBeAdded);
                    service.putExtra("recipeImageUrl", imageUri.toString());
                    getActivity().getApplicationContext().startService(service);
                }
                else{
                    Toast.makeText(getActivity(), messageBody, Toast.LENGTH_SHORT).show();
                }
            }
        });

        addImageButton = (Button) view.findViewById(R.id.add_image_button);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyStoragePermissions(getActivity())){
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            } else {
                Toast.makeText(getActivity(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if(reqCode == RESULT_LOAD_IMG){
            if (resultCode == RESULT_OK) {
                try {
                    imageUri = data.getData();
                    final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    recipeImageView.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    imageUri = null;
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Couldn't retrieve image", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(getActivity(), "No image was picked",Toast.LENGTH_LONG).show();
                imageUri = null;
            }
        }
    }

    private boolean dataValidation(){
        boolean textsValidated = false;
        boolean numbersValidated = false;
        boolean variousValidated = false;//Currently not in use but returned, thus has to be true for the moment.
        messageBody = "";

        //Strings validation
        if(!(recipeTitleEditText.getText().toString().trim().equals("")
                || recipeIngredientsEditText.getText().toString().trim().equals("")
                || recipeInstructionsEditText.getText().toString().trim().equals(""))){
            textsValidated = true;
        }else{
            messageBody = "Title, ingredients, instructions, ";
        }

        //Numbers validation
        if(!recipePreparationCostEditText.getText().toString().isEmpty()
                && !recipePreparationTimeEditText.getText().toString().isEmpty()) {
            double prepCost = Double.valueOf(recipePreparationCostEditText.getText().toString().trim());
            int prepTime = Integer.parseInt(recipePreparationTimeEditText.getText().toString().trim());
            if(prepCost != 0 && prepTime != 0 && difficultyRating > 0){
                numbersValidated = true;
            }
        }
        else{
            messageBody = messageBody + "cost, time, ";
        }

        if(imageUri != null){
            variousValidated = true;
        }
        else{
            messageBody = messageBody + "image, ";
        }
        if(messageBody.length() > 2) messageBody = messageBody.substring(0, messageBody.length() - 2);
        if(textsValidated && numbersValidated && !variousValidated)
        messageBody = messageBody + " is mandatory!";
        else messageBody = messageBody + " are mandatory!";
        messageBody = messageBody.substring(0, 1).toUpperCase() + messageBody.substring(1).toLowerCase();

        return textsValidated && numbersValidated && variousValidated;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        AddNewRecipeService.MyBinder binder = (AddNewRecipeService.MyBinder) service;
        addNewRecipeService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        addNewRecipeService = null;
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ServiceIsDone")) {
                String resultLocation = intent.getStringExtra("resultLocation");
                Toast.makeText(getActivity(), "Recipe was added in "
                        + resultLocation, Toast.LENGTH_SHORT).show();
            }
            if(intent.getAction().equals("ImageServiceIsDone")){
                Toast.makeText(getActivity(), "Image was uploaded ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user

            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }
        return true;
    }
}
