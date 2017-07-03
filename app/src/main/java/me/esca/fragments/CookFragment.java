package me.esca.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.net.URI;

import me.esca.R;
import me.esca.model.Recipe;
import me.esca.services.escaWS.recipes.AddNewRecipeService;

/**
 * Created by Me on 18/06/2017.
 */

public class CookFragment extends Fragment implements ServiceConnection {

    private EditText recipeTitleEditText;
    private EditText recipeIngredientsEditText;
    private EditText recipeInstructionsEditText;
    private EditText recipePreparationTimeEditText;
    private EditText recipePreparationCostEditText;
//    private Spinner recipeDifficultyRatingSpinner;
    private Button addRecipeButton;
    private Recipe recipeToBeAdded;
    private AddNewRecipeService addNewRecipeService;
    private DataUpdateReceiver dataUpdateReceiver;
    private SeekBar difficultyRatingSeekBar;
    private TextView difficultyTextView;
    private int difficultyRating;


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
                    addNewRecipeService = new AddNewRecipeService();
                    Intent service = new Intent(getActivity().getApplicationContext(), AddNewRecipeService.class);
                    service.putExtra("recipeToBeAdded", recipeToBeAdded);
                    getActivity().getApplicationContext().startService(service);
                }
                else{
                    Toast.makeText(getActivity(), "Title, ingredients and instructions mandatory.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean dataValidation(){
        boolean textsValidated = false;
        boolean numbersValidated = false;
        boolean variousValidated = true;//Currently not in use but returned, thus has to be true for the moment.

        //Strings validation
        if(!(recipeTitleEditText.getText().toString().trim().equals("")
                || recipeIngredientsEditText.getText().toString().trim().equals("")
                || recipeInstructionsEditText.getText().toString().trim().equals(""))){
            textsValidated = true;
        }

        //Numbers validation
        if(!recipePreparationCostEditText.getText().toString().isEmpty()
                && !recipePreparationCostEditText.getText().toString().isEmpty()) {
            double prepCost = Double.valueOf(recipePreparationCostEditText.getText().toString().trim());
            int prepTime = Integer.parseInt(recipePreparationTimeEditText.getText().toString().trim());
            if(prepCost != 0 && prepTime != 0 && difficultyRating > 0){
                numbersValidated = true;
            }
        }
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
            String resultLocation = intent.getStringExtra("resultLocation");
            if (intent.getAction().equals("ServiceIsDone")) {
                Toast.makeText(getActivity(), "Recipe was added in "
                        + resultLocation, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
