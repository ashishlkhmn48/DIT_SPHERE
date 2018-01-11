package com.ashishlakhmani.dit_sphere.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.classes.CalculatorHelperSgpa;
import com.ashishlakhmani.dit_sphere.classes.ResultHelper;

import java.util.List;


public class ResultViewerAdapter extends RecyclerView.Adapter {

    private Context context;

    private List<ResultHelper> list;
    private List<CalculatorHelperSgpa> answerList;
    private int SIZE_TO_MATCH;
    private String semester;


    public ResultViewerAdapter(Context context, List<ResultHelper> list, List<CalculatorHelperSgpa> answerList, int SIZE_TO_MATCH, String semester) {
        this.context = context;
        this.list = list;
        this.answerList = answerList;
        this.SIZE_TO_MATCH = SIZE_TO_MATCH;
        this.semester = semester;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_viewer, parent, false);
        return new ResultViewerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String name = list.get(position).getName();
        String code = list.get(position).getCode();
        String credit = list.get(position).getCredit();
        String grade = list.get(position).getGrade();

        ((MyViewHolder) holder).name.setText(name);
        ((MyViewHolder) holder).code.setText(code);
        ((MyViewHolder) holder).credit.setText(credit);
        ((MyViewHolder) holder).grade.setText(grade);

        //To ensure that when complete result is loaded then only long press works.
        //**Very Important.
        ((MyViewHolder) holder).card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (list.size() == SIZE_TO_MATCH) {
                    calculation(answerList);
                }else {
                    Toast.makeText(context, "Results are not Completely loaded.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, code, credit, grade;
        CardView card;

        private MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            code = itemView.findViewById(R.id.code);
            credit = itemView.findViewById(R.id.credit);
            grade = itemView.findViewById(R.id.grade);
            card = itemView.findViewById(R.id.card);
        }
    }


    //SGPA Calculation logic.
    private void calculation(List<CalculatorHelperSgpa> answerList) {
        double totalCredits = 0;
        double totalGradePointsCredits = 0;
        for (int i = 0; i < answerList.size(); i++) {
            if (answerList.get(i).getGradePoints() == -1) {
                Toast.makeText(context, "Please Enter Valid Grade.", Toast.LENGTH_LONG).show();
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

        String sem = null;
        if (semester.equals("1")) {
            sem = "1st Semester";
        } else if (semester.equals("2")) {
            sem = "2nd Semester";
        } else if (semester.equals("3")) {
            sem = "3rd Semester";
        } else {
            sem = semester + "th Semester";
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Result");
        builder.setMessage("Your " + sem + " Result :  " + answer);

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final String result = String.valueOf(answer);
        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shareTask(result);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void shareTask(String result) {
        String sem = null;
        if (semester.equals("1")) {
            sem = "1st Semester";
        } else if (semester.equals("2")) {
            sem = "2nd Semester";
        } else if (semester.equals("3")) {
            sem = "3rd Semester";
        } else {
            sem = semester + "th Semester";
        }

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "My " + sem + " Result :  " + result);
        context.startActivity(Intent.createChooser(sharingIntent, "Share News Using"));
    }

}
