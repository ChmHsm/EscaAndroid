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
import android.widget.Spinner;

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
    private Spinner recipeDifficultyRatingSpinner;
    private Button addRecipeButton;
    private Recipe recipeToBeAdded;
    private AddNewRecipeService addNewRecipeService;
    private DataUpdateReceiver dataUpdateReceiver;


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
        recipeDifficultyRatingSpinner = (Spinner)view.findViewById(R.id.difficulty_rating_spinner);

        addRecipeButton = (Button) view.findViewById(R.id.add_recipe_button);
        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataValidation()){
                    recipeToBeAdded = new Recipe(recipeTitleEditText.getText().toString(), 0, 0,
                            0, recipeIngredientsEditText.getText().toString(),
                            recipeInstructionsEditText.getText().toString(), null, null, null, null);
                    addNewRecipeService.setRecipeToBeAdded(recipeToBeAdded);
                    Intent service = new Intent(getActivity().getApplicationContext(), AddNewRecipeService.class);
                    getActivity().getApplicationContext().startService(service);
                }
            }
        });

        return view;
    }

    private boolean dataValidation(){
        return !(recipeTitleEditText.getText().toString().equals("")
                || recipeIngredientsEditText.getText().toString().equals("")
                || recipeInstructionsEditText.getText().toString().equals(""));

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
                // Do stuff - maybe update my view based on the changed DB contents
            }
        }
    }
}
