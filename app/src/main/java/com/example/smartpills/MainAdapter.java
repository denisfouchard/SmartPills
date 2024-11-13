package com.example.smartpills;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.example.smartpills.database.PilulierDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainAdapter extends BaseExpandableListAdapter {

    private ArrayList<PilulierDatabase> mySmartPills;
    private HashMap<PilulierDatabase, ArrayList<String>> listChild;
    private ArrayList<Boolean> takenTab;
    ItemClickListener itemClickListener;


    public MainAdapter(ArrayList<PilulierDatabase> mySmartPills, ArrayList<Boolean>takenTab, HashMap<PilulierDatabase, ArrayList<String>> listChild, ItemClickListener itemClickListener){
        this.mySmartPills = mySmartPills;
        this.listChild = listChild;
        this.itemClickListener = itemClickListener;
        this.takenTab = takenTab;
    }


    @Override
    public int getGroupCount() {
        return mySmartPills.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChild.get(mySmartPills.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mySmartPills.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listChild.get(mySmartPills.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main, parent, false);

        TextView textView = convertView.findViewById(R.id.name);
        PilulierDatabase smartPills = mySmartPills.get(groupPosition);
        String sGroup = smartPills.getUtlisateur();
        textView.setText(sGroup);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(Color.BLUE);

        TextView statusText = convertView.findViewById(R.id.statusTextUpToDate);
        ImageView statusIcon = convertView.findViewById(R.id.statusIcon);

        boolean taken = takenTab.get(groupPosition);

        if (taken) {
            statusText.setText("A jour");
            statusIcon.setBackground(AppCompatResources.getDrawable(convertView.getContext(), R.drawable.fui_ic_check_circle_black_128dp));
            statusIcon.setVisibility(View.VISIBLE);


        } else {
            statusText.setText("Alerte");
            statusText.setError("");
            //AppCompatResources.getDrawable(convertView.getContext(), R.drawable.button_radius_error_color);
            statusIcon.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_selectable_list_item, parent, false);

        TextView textView = convertView.findViewById(android.R.id.text1);
        String sChild = String.valueOf(getChild(groupPosition, childPosition));
        textView.setText(sChild);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(sChild){
                    case "Appeler":
                        String phoneNumber = mySmartPills.get(groupPosition).getphoneNumber();
                        itemClickListener.onClick(phoneNumber);
                        break;
                    case "Voir Historique":
                        itemClickListener.onClick(groupPosition, false);
                        break;
                    case "Modifier":
                        itemClickListener.onClick(groupPosition, true);
                        break;
                    case "Supprimer":
                        itemClickListener.onClick(groupPosition, 'd');
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
