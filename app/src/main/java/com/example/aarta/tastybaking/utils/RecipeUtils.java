package com.example.aarta.tastybaking.utils;

import android.content.Context;
import android.support.v4.widget.CircularProgressDrawable;
import android.widget.TextView;

import com.example.aarta.tastybaking.R;
import com.example.aarta.tastybaking.data.models.Recipe;
import com.example.aarta.tastybaking.data.models.Step;

import java.util.List;

public final class RecipeUtils {

    public static Recipe[] getRecipeArray(List<Recipe> recipeList) {
        // there might be missed IDs, so rewrite them in order
        rewriteStepIDOrder(recipeList);
        Recipe[] recipeArray = new Recipe[recipeList.size()];
        recipeList.toArray(recipeArray); // fill the array
        return recipeArray;
    }

    private static void rewriteStepIDOrder(List<Recipe> recipeList) {
        int recipesNum = recipeList.size();
        for (int i = 0; i < recipesNum; i++) {
            List<Step> currentSteps = recipeList.get(i).getSteps();
            int stepsNum = currentSteps.size();
            for (int x = 0; x < stepsNum; x++) {
                currentSteps.get(x).setId(x);
            }
        }
    }

    public static void setFormattedDescription(Step oneStep, TextView tvLongDescr, TextView tvShortDescr) {
        tvShortDescr.setText(oneStep.getShortDescription());
        String currentID = String.valueOf(oneStep.getId());
        String idOfLongDescr = oneStep.getDescription().substring(0, 3);
        String restOfLongDescr = oneStep.getDescription().substring(1);
        if (!idOfLongDescr.equals(currentID) && isInteger(idOfLongDescr)) {
            String correctLongDescr = currentID + ". " + restOfLongDescr;
            tvLongDescr.setText(correctLongDescr);
        } else
            tvLongDescr.setText(oneStep.getDescription());
    }

    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static CircularProgressDrawable getCircleProgressDrawable(Context ctx, float strokeWidth, float centerRadius) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(ctx);
        circularProgressDrawable.setStrokeWidth(strokeWidth);
        circularProgressDrawable.setCenterRadius(centerRadius);
        int secondaryColor = ctx.getResources().getColor(R.color.colorSecondary);
        int primaryDarkColor = ctx.getResources().getColor(R.color.colorPrimaryDark);
        circularProgressDrawable.setColorSchemeColors(secondaryColor, primaryDarkColor);
        circularProgressDrawable.start();
        return circularProgressDrawable;
    }
}
