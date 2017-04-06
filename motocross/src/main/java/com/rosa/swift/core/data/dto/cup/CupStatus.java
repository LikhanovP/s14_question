package com.rosa.swift.core.data.dto.cup;

import android.support.annotation.Nullable;

import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.network.responses.photosession.CupStatusResponse;

import java.util.Calendar;
import java.util.Date;

/**
 * Представляет статус фотосессии ЦУП
 */
public class CupStatus {

    /**
     * Запланированное время фотосессии
     */
    private Date mCupDate;

    /**
     * Время сервера в момент получения информации по статусу
     */
    private Date mCurrentDate;

    /**
     * Была ли фотосессия отложена ранее
     */
    private boolean mCupWasDelayed;

    /**
     * Количество миллисекунд за которое будет доступна фотосессия
     */
    private long mCupAvailableForMillis;

    /**
     * Возвращает запланированную дату выполнения фотосессии ЦУП
     */
    @Nullable
    public Date getCupDate() {
        return mCupDate;
    }

    /**
     * Возвращает значение, показывающее, была ли фотосессия отложена ранее
     */
    public boolean isCupWasDelayed() {
        return mCupWasDelayed;
    }

    /**
     * Возвращает значение, показывающее, доступна ли для выполнения фотосессия ЦУП
     * относительно заданного времени
     *
     * @param millis Время в миллисекундах
     */
    public boolean isAvailableForTime(long millis) {
        return mCupAvailableForMillis != 0 && mCupAvailableForMillis > millis;
    }

    /**
     * Возвращает время в миллисекундах до начала выполнения фотосессии
     * от текущего времени на сервере
     */
    public long getTimeToNextCup() {
        return mCupDate != null && mCurrentDate != null ?
                mCupDate.getTime() - mCurrentDate.getTime() :
                Constants.STOP_TIME_FOR_CUP_SESSION;
    }

    /**
     * Инициализирует экземпляр класса
     *
     * @param cupStatusRes Модель ответа от сервера на запрос статуса для фотосессии ЦУП
     */
    public CupStatus(CupStatusResponse cupStatusRes) {
        if (cupStatusRes != null) {
            //получаем время фотосессии ЦУП и время на сервере
            mCupDate = StringUtils.getDateFromSapDateTime(cupStatusRes.getCupDate(), cupStatusRes.getCupTime());
            mCurrentDate = StringUtils.getDateFromSapDateTime(cupStatusRes.getCurrentDate(),
                    cupStatusRes.getCurrentTime());
            if (mCupDate != null && mCurrentDate != null) {
                int cupAvailableDays = cupStatusRes.getCupAvailableDays();
                //из даты вычитаем дни, за которые фотосессия становится доступна, и обнуляем время
                Calendar cupAvailableDate = Calendar.getInstance();
                cupAvailableDate.setTime(mCupDate);
                cupAvailableDate.add(Calendar.DAY_OF_MONTH, -cupAvailableDays);
                cupAvailableDate.set(Calendar.HOUR_OF_DAY, 0);
                cupAvailableDate.set(Calendar.MINUTE, 0);
                cupAvailableDate.set(Calendar.SECOND, 0);
                //из даты фотосессии вычитаем дату, когда фотосессия будет доступна
                //и получим количество миллисекунд за которое фотосессия станет доступна
                mCupAvailableForMillis = mCupDate.getTime() - cupAvailableDate.getTimeInMillis();
                mCupWasDelayed = cupStatusRes.isCupWasDelayed();
            }
        }
    }
}
