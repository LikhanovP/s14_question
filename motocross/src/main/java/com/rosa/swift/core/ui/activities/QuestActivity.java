package com.rosa.swift.core.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rd.PageIndicatorView;
import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.business.utils.DialogHandler;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.common.Warehouse;
import com.rosa.swift.core.data.dto.quest.StorageLocationAnswer;
import com.rosa.swift.core.network.requests.quest.DriverAnswerRequest;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.mvp.shift.quiz.repositories.AnswerDto;
import com.rosa.swift.mvp.shift.quiz.repositories.QuestDto;

import java.util.ArrayList;
import java.util.List;

public class QuestActivity extends LogonlessActivity {

    private List<AnswerDto> answers;
    private List<StorageLocationAnswer> dr_answers;
    private List<QuestDto> quest;
    private String mDeliveryNumber;
    private List<Warehouse> lgorts;
    private Button prevBtn;
    private Button nextBtn;
    private Button sendBtn;
    private TextView lgortText;
    private int current_lgort = -1;
    private int lgort_count = 0;
    private ListView answersListView;
    private PageIndicatorView piv;

    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        Intent intent = this.getIntent();
        quest = (List<QuestDto>) intent.getSerializableExtra("quest");
        mDeliveryNumber = intent.getStringExtra("delivery");
        lgorts = (ArrayList<Warehouse>) intent.getSerializableExtra("lgorts");

        viewPager = (ViewPager) findViewById(R.id.myviewpager);
        pagerAdapter = new PagerAdapter();
        viewPager.setAdapter(pagerAdapter);

        piv = (PageIndicatorView) findViewById(R.id.pageIndicator);

        /*TextView quest_text = (TextView) this.findViewById(R.id.questTextView);
        answersListView = (ListView) findViewById(R.id.answersListView);
        answersListView.setOnItemClickListener((parent, view, position, id) -> selectAnswer(position));
        lgortText = (TextView) findViewById(R.id.lgortTextView);
        prevBtn = (Button) findViewById(R.id.answerPrev);
        nextBtn = (Button) findViewById(R.id.answerNext);
        sendBtn = (Button) findViewById(R.id.answerBtn);

        prevBtn.setOnClickListener(v -> selectLgort(current_lgort - 1));
        nextBtn.setOnClickListener(v -> selectLgort(current_lgort + 1));
        sendBtn.setOnClickListener(v -> sendAnswers(false));


        if (quest != null) {
            quest_text.setText(quest.getText());
            dr_answers = new ArrayList<>();
            answers = DataRepository.getInstance().getAnswersForQuestId(quest.getId());
            if (StringUtils.isNullOrEmpty(quest.getWarehouse()) || lgorts == null) { //без складов или склады не подгрузились
                dr_answers.add(new StorageLocationAnswer(null, -1));
            } else {
                if (!StringUtils.isNullOrEmpty(mDeliveryNumber) && lgorts != null) {
                    for (Warehouse warehouse : lgorts) {
                        dr_answers.add(new StorageLocationAnswer(warehouse, -1));
                    }
                }
            }
            List<String> str_answers = new ArrayList<String>();
            for (AnswerDto ja : answers)
                str_answers.add(ja.toString());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.answer_text_layout, str_answers);
            answersListView.setAdapter(adapter);
            answersListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            lgort_count = dr_answers.size();
        }

        selectLgort(0);*/
    }

    private void selectLgort(int lgort_num) {
        if (lgort_num < 0 || lgort_num > lgort_count) return;
        current_lgort = lgort_num;
        int i = 0;
        StorageLocationAnswer locationAnswer = dr_answers.get(lgort_num);
        lgortText.setText(locationAnswer.getWarehouse() != null ?
                locationAnswer.getWarehouse().getName() : "");
        for (AnswerDto ja : answers)
            answersListView.setItemChecked(i++, ja.getAnswerId() == locationAnswer.getSelectedAnswer());
        updateButtonsVisibility();
    }

    private void updateButtonsVisibility() {
        if (current_lgort == 0)
            prevBtn.setVisibility(View.INVISIBLE);
        else
            prevBtn.setVisibility(View.VISIBLE);
        if (current_lgort == (lgort_count - 1))
            nextBtn.setVisibility(View.INVISIBLE);
        else
            nextBtn.setVisibility(View.VISIBLE);
    }

    private void sendAnswers(boolean with_confirm) {
        if (with_confirm) {
            CommonUtils.confirm(QuestActivity.this, R.string.answer_btn_text, R.string.answer_confirm_text, new DialogHandler() {
                @Override
                public void YesClick() {
                    sendAnswers(false);
                }

                @Override
                public void NoClick() {

                }
            });
        } else {
            for (StorageLocationAnswer locationAnswer : dr_answers) {
                if (locationAnswer.getSelectedAnswer() == -1) {
                    Toast.makeText(QuestActivity.this, "Вы ответили не на все вопросы", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            showProgress(R.string.send_data_progress);
            try {
                if (answers.size() > 0) {
                    DriverAnswerRequest request = new DriverAnswerRequest(quest.get(current_lgort).getId(),
                            TextUtils.isEmpty(mDeliveryNumber) ? "" : mDeliveryNumber, dr_answers);
                    SapRequestUtils.sendDriverAnswer(request, new ServiceCallback() {
                        @Override
                        public void onEndedRequest() {
                            hideProgress();
                        }

                        @Override
                        public void onFinished(String evParams) {
                            if (!evParams.equals("X"))
                                CommonUtils.ShowErrorMessage(QuestActivity.this, "Не удалось отправить ответ!");
                            else {
                                Toast.makeText(QuestActivity.this, "Отправлено успешно", Toast.LENGTH_SHORT).show();
                                quest.remove(current_lgort);

                                Toast.makeText(QuestActivity.this, String.valueOf(quest.size()), Toast.LENGTH_SHORT).show();
                                pagerAdapter = new PagerAdapter();
                                viewPager.setAdapter(pagerAdapter);
                            }
                            if (quest.size() == 0) {
                                QuestActivity.this.finish();
                            }
                            //TODO: ipopov 25.03.2017 обработка вопросов
                            //GlobalContext.getInstance().resetAskQuest(quest);
                        }

                        @Override
                        public void onFinishedWithException(WSException ex) {
                            CommonUtils.ShowErrorMessage(QuestActivity.this, ex.getMessage());
                            QuestActivity.this.finish();
                        }

                        @Override
                        public void onCancelled() {
                            hideProgress();
                        }
                    });
                }
            } catch (Exception exception) {
                Log.e(exception);
            }
        }
    }

    private void selectAnswer(int pos) {
        dr_answers = new ArrayList<>();
        answers = DataRepository.getInstance().getAnswersForQuestId(quest.get(current_lgort).getId());
        if (StringUtils.isNullOrEmpty(quest.get(current_lgort).getWarehouse()) || lgorts == null) { //без складов или склады не подгрузились
            dr_answers.add(new StorageLocationAnswer(null, -1));
        } else {
            if (!StringUtils.isNullOrEmpty(mDeliveryNumber) && lgorts != null) {
                for (Warehouse warehouse : lgorts) {
                    dr_answers.add(new StorageLocationAnswer(warehouse, -1));
                }
            }
        }
        lgort_count = dr_answers.size();

        StorageLocationAnswer locationAnswer = dr_answers.get(0);
        locationAnswer.setSelectedAnswer(answers.get(pos).getAnswerId());

        sendAnswers(true);
        /*if (current_lgort != (lgort_count - 1))
            selectLgort(current_lgort + 1);
        else
            sendAnswers(true);*/
    }

    @Override
    public void onBackPressed() {
        sendAnswers(true);
    }

    public class PagerAdapter extends android.support.v4.view.PagerAdapter{

        int countPages = quest.size();

        @Override
        public int getCount() {
            return countPages;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView textView = new TextView(QuestActivity.this);
            textView.setTextSize(20);
            textView.setPadding(10, 40, 10, 40);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setText(quest.get(position).getText());

            ListView questList = new ListView(QuestActivity.this);
            questList.setPadding(40, 0, 40, 0);
            ViewGroup.LayoutParams layoutPar = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            questList.setLayoutParams(layoutPar);

            answers = new ArrayList<>();
            answers = DataRepository.getInstance().getAnswersForQuestId(quest.get(position).getId());

            List<String> str_answers = new ArrayList<String>();
            for (AnswerDto ja : answers)
                str_answers.add(ja.toString());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(QuestActivity.this, android.R.layout.simple_list_item_single_choice, str_answers);
            questList.setAdapter(adapter);
            questList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

            LinearLayout layout = new LinearLayout(QuestActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.setBackgroundColor(Color.WHITE);
            layout.setLayoutParams(layoutParams);
            layout.addView(textView);
            layout.addView(questList);

            final int page = position;
            layout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Toast.makeText(QuestActivity.this,
                            "Page " + quest.get(page).getText() + " clicked",
                            Toast.LENGTH_LONG).show();
                }});

            questList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    current_lgort = page;
                    selectAnswer(position);

                    //Toast.makeText(QuestActivity.this, String.valueOf(page) + "   " + String.valueOf(position), Toast.LENGTH_LONG).show();

                    /*Toast.makeText(QuestActivity.this,
                            "Page " + quest.get(page).getText() + " clicked",
                            Toast.LENGTH_LONG).show();

                    Toast.makeText(QuestActivity.this,
                            "Page " + ((TextView)view).getText() + " clicked",
                            Toast.LENGTH_LONG).show();*/
                }
            });

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout)object);
        }
    }
}
