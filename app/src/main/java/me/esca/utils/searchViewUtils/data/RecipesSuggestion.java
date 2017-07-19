package me.esca.utils.searchViewUtils.data;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by Me on 19/07/2017.
 */

public class RecipesSuggestion implements SearchSuggestion {

    private String recipeName;
    private boolean isHistory;

    public RecipesSuggestion(String suggestion) {
        this.recipeName = suggestion.toLowerCase();
    }

    public RecipesSuggestion(Parcel source) {
        this.recipeName = source.readString();
        this.isHistory = source.readInt() != 0;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }

    @Override
    public String getBody() {
        return recipeName;
    }

    public static final Creator<RecipesSuggestion> CREATOR = new Creator<RecipesSuggestion>() {
        @Override
        public RecipesSuggestion createFromParcel(Parcel source) {
            return new RecipesSuggestion(source);
        }

        @Override
        public RecipesSuggestion[] newArray(int size) {
            return new RecipesSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipeName);
        dest.writeInt(isHistory ? 1 : 0);
    }
}
