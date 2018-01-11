package com.ashishlakhmani.dit_sphere.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.ashishlakhmani.dit_sphere.classes.CalculatorHelperCgpa;
import com.ashishlakhmani.dit_sphere.classes.CalculatorHelperSgpa;

import java.util.ArrayList;
import java.util.List;


public class Calculator extends Fragment {

    RadioButton sgpa, cgpa;
    TextView textView;
    Spinner spinner;
    LinearLayout linearLayout;
    Button button;

    private List<Integer> list = new ArrayList<>();
    private List<View> view_list_sgpa = new ArrayList<>();
    private List<View> view_list_cgpa = new ArrayList<>();

    public Calculator() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);

        initialize(view);
        radioButtonTask();
        spinnerTask();
        buttonTask();

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getFragmentManager() != null) {
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        ViewCompat.setElevation(((HomeActivity) getActivity()).bottomNavigationView, 0);

                    } else {
                        ViewCompat.setElevation(((HomeActivity) getActivity()).bottomNavigationView, 10);
                        ((HomeActivity) getActivity()).setToolbarTitle("News");
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        ((HomeActivity) getActivity()).setToolbarTitle("Pointer Calculator");
        super.onResume();
    }


    private void initialize(View view) {
        sgpa = view.findViewById(R.id.credit);
        cgpa = view.findViewById(R.id.cgpa);
        textView = view.findViewById(R.id.display_text);
        spinner = view.findViewById(R.id.spinner);
        linearLayout = view.findViewById(R.id.linearLayout);
        button = view.findViewById(R.id.check);

    }

    private void radioButtonTask() {

        sgpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sgpa.isChecked()) {
                    textView.setText("Select Number of Subjects :");
                    button.setText("Check  S.G.P.A");
                    spinner.setSelection(0);
                    spinnerTask();
                }
            }
        });

        cgpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cgpa.isChecked()) {
                    textView.setText("Select Number of Semesters :");
                    button.setText("Check  C.G.P.A");
                    spinner.setSelection(0);
                    spinnerTask();
                }
            }
        });

    }


    private void spinnerTask() {

        if (sgpa.isChecked()) {

            if (!list.isEmpty()) {
                list.clear();
            }


            for (int i = 1; i < 9; i++) {
                list.add(i);
            }
            //Putting items in branch spinner
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    int num = list.get(position);

                    if (linearLayout.getChildCount() > 0)
                        linearLayout.removeAllViews();

                    if (view_list_sgpa.size() > 0)
                        view_list_sgpa.clear();

                    for (int i = 1; i <= num; i++) {
                        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_sgpa, linearLayout, false);
                        view_list_sgpa.add(v);

                        TextView subNum = v.findViewById(R.id.sub_num);
                        if (i == 1)
                            subNum.setText(String.valueOf(i) + "st" + " Subject");
                        else if (i == 2)
                            subNum.setText(String.valueOf(i) + "nd" + " Subject");
                        else if (i == 3)
                            subNum.setText(String.valueOf(i) + "rd" + " Subject");
                        else
                            subNum.setText(String.valueOf(i) + "th" + " Subject");

                        linearLayout.addView(v);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    if (linearLayout.getChildCount() > 0)
                        linearLayout.removeAllViews();

                    if (view_list_sgpa.size() > 0)
                        view_list_sgpa.clear();
                }

            });
        }

        if (cgpa.isChecked()) {

            if (!list.isEmpty()) {
                list.clear();
            }

            for (int i = 1; i < 9; i++) {
                list.add(i);
            }
            //Putting items in branch spinner
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    int num = list.get(position);

                    if (linearLayout.getChildCount() > 0)
                        linearLayout.removeAllViews();

                    if (view_list_cgpa.size() > 0)
                        view_list_cgpa.clear();

                    for (int i = 1; i <= num; i++) {
                        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_cgpa, linearLayout, false);
                        view_list_cgpa.add(v);

                        TextView semNum = v.findViewById(R.id.sem_num);
                        if (i == 1)
                            semNum.setText(String.valueOf(i) + "st" + " Semester");
                        else if (i == 2)
                            semNum.setText(String.valueOf(i) + "nd" + " Semester");
                        else if (i == 3)
                            semNum.setText(String.valueOf(i) + "rd" + " Semester");
                        else
                            semNum.setText(String.valueOf(i) + "th" + " Semester");

                        linearLayout.addView(v);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    if (linearLayout.getChildCount() > 0)
                        linearLayout.removeAllViews();

                    if (view_list_cgpa.size() > 0)
                        view_list_cgpa.clear();
                }

            });
        }
    }


    private void buttonTask() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sgpa.isChecked()) {
                    if (checkEditTextsForSgpa()) {
                        List<CalculatorHelperSgpa> answerList = new ArrayList<>();
                        for (int i = 0; i < view_list_sgpa.size(); i++) {
                            EditText et1 = view_list_sgpa.get(i).findViewById(R.id.credit);
                            EditText et2 = view_list_sgpa.get(i).findViewById(R.id.grade);

                            double credit = Double.valueOf(et1.getText().toString());
                            String grade = et2.getText().toString().toUpperCase();

                            answerList.add(new CalculatorHelperSgpa(credit, grade));
                        }

                        calculationSgpa(answerList);
                    } else {
                        Toast.makeText(getContext(), "Please Fill All Fields.", Toast.LENGTH_SHORT).show();
                    }
                }

                if (cgpa.isChecked()) {
                    if (checkEditTextsForCgpa()) {
                        List<CalculatorHelperCgpa> answerList = new ArrayList<>();
                        for (int i = 0; i < view_list_cgpa.size(); i++) {
                            EditText et1 = view_list_cgpa.get(i).findViewById(R.id.sgpa);
                            EditText et2 = view_list_cgpa.get(i).findViewById(R.id.total_credits);

                            double sgpa = Double.valueOf(et1.getText().toString());
                            double totalCredits = Double.valueOf(et2.getText().toString());

                            answerList.add(new CalculatorHelperCgpa(sgpa, totalCredits));
                        }

                        calculationCgpa(answerList);
                    } else {
                        Toast.makeText(getContext(), "Please Fill All Fields.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //SGPA calculation logic.
    private void calculationSgpa(List<CalculatorHelperSgpa> answerList) {
        double totalCredits = 0;
        double totalGradePointsCredits = 0;
        for (int i = 0; i < answerList.size(); i++) {
            if (answerList.get(i).getGradePoints() == -1) {
                Toast.makeText(getContext(), "Please Enter Valid Grade.", Toast.LENGTH_LONG).show();
                return;
            }
            totalCredits = totalCredits + answerList.get(i).getCredit();
            totalGradePointsCredits = totalGradePointsCredits + (answerList.get(i).getCredit() * answerList.get(i).getGradePoints());
        }

        double sgpa = totalGradePointsCredits / totalCredits;
        String answer = String.valueOf(sgpa);

        if (answer.length() >= 4) {
            answer = answer.substring(0, 4);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Result");
        builder.setMessage("Your S.G.P.A  is : " + answer);
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //CGPA calculation logic.
    private void calculationCgpa(List<CalculatorHelperCgpa> answerList) {
        double totalCredits = 0;
        double totalSgpaCredits = 0;
        for (int i = 0; i < answerList.size(); i++) {
            if (answerList.get(i).getSgpa() > 10 || answerList.get(i).getSgpa() < 0) {
                Toast.makeText(getContext(), "Please Enter Valid  S.G.P.A.", Toast.LENGTH_LONG).show();
                return;
            }
            totalCredits = totalCredits + answerList.get(i).getTotalCredits();
            totalSgpaCredits = totalSgpaCredits + (answerList.get(i).getTotalCredits() * answerList.get(i).getSgpa());
        }

        double sgpa = totalSgpaCredits / totalCredits;
        String answer = String.valueOf(sgpa);

        if (answer.length() >= 4) {
            answer = answer.substring(0, 4);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Result");
        builder.setMessage("Your current C.G.P.A is : " + answer);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //Check if any editText for SGPA is empty or not.
    private boolean checkEditTextsForSgpa() {
        for (int i = 0; i < view_list_sgpa.size(); i++) {
            EditText et1 = view_list_sgpa.get(i).findViewById(R.id.credit);
            EditText et2 = view_list_sgpa.get(i).findViewById(R.id.grade);

            if (et1.getText().toString().trim().isEmpty() || et2.getText().toString().trim().isEmpty())
                return false;
        }
        return true;
    }

    //Check if any editText for CGPA is empty or not.
    private boolean checkEditTextsForCgpa() {
        for (int i = 0; i < view_list_cgpa.size(); i++) {
            EditText et1 = view_list_cgpa.get(i).findViewById(R.id.sgpa);
            EditText et2 = view_list_cgpa.get(i).findViewById(R.id.total_credits);

            if (et1.getText().toString().trim().isEmpty() || et2.getText().toString().trim().isEmpty())
                return false;
        }
        return true;
    }




}
