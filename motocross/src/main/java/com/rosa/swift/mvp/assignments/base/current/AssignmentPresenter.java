package com.rosa.swift.mvp.assignments.base.current;

import android.text.TextUtils;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.gson.Gson;
import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.deliveries.TransportationType;
import com.rosa.swift.core.network.json.sap.assign.JChangeOut;
import com.rosa.swift.core.network.requests.delivery.ChangeDeliveryRequest;
import com.rosa.swift.core.network.requests.function.GateRequest;
import com.rosa.swift.core.network.requests.message.DriverMessageRequest;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.mvp.assignments.base.repository.AssignmentRepository;
import com.rosa.swift.mvp.assignments.base.repository.DriverMessagesRepository;
import com.rosa.swift.mvp.assignments.base.repository.Dto.AssignmentDto;
import com.rosa.swift.mvp.assignments.base.repository.Dto.DriverMessageDto;
import com.rosa.swift.mvp.assignments.base.repository.IAssignmentRepository;
import com.rosa.swift.mvp.assignments.base.repository.IDriverMessagesRepository;

import java.util.List;

@InjectViewState
public class AssignmentPresenter extends MvpPresenter<IAssignmentView> {

    private IAssignmentRepository mAssignmentProvider = new AssignmentRepository();

    private IDriverMessagesRepository mDriverMessagesProvider = new DriverMessagesRepository();

    private AssignmentDto mAssignmentDto;

    private List<DriverMessageDto> mDriverMessages;

    private String mAssignmentNumber;

    private DriverMessageDto mDriverMessage;

    private DataRepository mDataRepository;

    //region LifeCycle

    @Override
    public void attachView(IAssignmentView view) {
        super.attachView(view);
        mDriverMessages = mDriverMessagesProvider.getMessages();
        mDataRepository = DataRepository.getInstance();
    }

    //endregion

    //region ViewEvent

    //region Show

    protected void showAssignmentDescription(String number, TransportationType type) {
        mAssignmentDto = mAssignmentProvider.getAssignmentByNumber(number, type);
        mAssignmentNumber = mAssignmentDto.getNumber();
        getViewState().showDescription(mAssignmentDto.getHtmlDescriptionCurrent());
    }

    //endregion

    //region SendPrint

    public void onSendPrintClick() {
        getViewState().showConfirmSendPrintDialog();
    }

    public void sendPrint(String deliveryNumber) {
        if (!TextUtils.isEmpty(deliveryNumber)) {
            getViewState().showProgress();
            ChangeDeliveryRequest request = new ChangeDeliveryRequest(deliveryNumber);
            SapRequestUtils.printDelivery(request, new ServiceCallback() {
                @Override
                public void onEndedRequest() {
                    getViewState().hideProgress();
                }

                @Override
                public void onFinished(String param) {
                    if (!TextUtils.isEmpty(param)) {
                        getViewState().showMessage(param);
                    }
                }

                @Override
                public void onFinishedWithException(WSException ex) {
                    getViewState().showError(ex.getMessage());
                }

                @Override
                public void onCancelled() {
                    getViewState().hideProgress();
                }
            });
        }
    }

    //endregion

    //region CompleteAssignment

    public void onFinishAssignmentClick() {
        getViewState().showConfirmCompleteDialog();
    }

    public void finishAssignment(String deliveryNumber) {
        if (!TextUtils.isEmpty(deliveryNumber)) {
            getViewState().showProgress();
            ChangeDeliveryRequest request = new ChangeDeliveryRequest(deliveryNumber);
            SapRequestUtils.finishDelivery(request, new ServiceCallback() {
                        @Override
                        public void onEndedRequest() {
                            getViewState().hideProgress();
                        }

                        @Override
                        public void onCancelled() {
                            getViewState().hideProgress();
                            //TODO: ipopov 19.02.2017 processMessages()
                            //unsuspend();
                        }

                        @Override
                        public void onFinished(String param) {
                            try {
                                JChangeOut changeOut = new Gson().fromJson(param, JChangeOut.class);
                                String errorMessage = changeOut.getErrorMessage();
                                if (TextUtils.isEmpty(errorMessage)) {
                                    mDataRepository.removeTransportation(deliveryNumber,
                                            TransportationType.Relocation);
                                    getViewState().showToast(R.string.finish_delivery_success);
                                    getViewState().close();
                                    //TODO: ipopov 19.02.2017 для чего manual_select?
                                    //manual_select = false;
                                    //TODO: ipopov 19.02.2017 перевести экран вопроса на фрагмент и MVP
                                    //askRequiredQuestion(jd);
                                    //TODO: ipopov 19.02.2017
                                    //if (currentDeliveryList.size() != 0) {
                                    //    deliveryInWork = null;
                                    //    switchStateTo(MainActivity.MainActivityState.OnDeliveryList);
                                    //} else
                                    //    startListeningFromTheBeginning();
                                } else {
                                    getViewState().showError(errorMessage);
                                }
                            } catch (Exception exception) {
                                getViewState().showError(R.string.relocation_complete_error);
                                Log.e(exception);
                            }
                        }

                        @Override
                        public void onFinishedWithException(WSException ex) {
                            getViewState().showError(ex.getMessage());
                        }
                    }

            );
        }
    }

    //endregion

    //region SendMessage

    public void onSendMessageClick() {
        if (!TextUtils.isEmpty(mAssignmentNumber) && mDriverMessages.size() > 0) {
            getViewState().showMessageTypesDialog(mDriverMessages);
        }
    }

    public void selectType(DriverMessageDto driverMessage) {
        mDriverMessage = driverMessage;
        getViewState().showCommentDialog();
    }

    public void sendMessage(String comment) {
        getViewState().showProgress();
        DriverMessageRequest request = new DriverMessageRequest(mDriverMessage.getType(), comment, mAssignmentNumber);
        SapRequestUtils.sendDriverMessage(request, new ServiceCallback() {
            @Override
            public void onEndedRequest() {
                getViewState().hideProgress();
            }

            @Override
            public void onCancelled() {
                getViewState().hideProgress();
            }

            @Override
            public void onFinished(String param) {
                if (param.equals(Constants.SAP_TRUE_FLAG)) {
                    getViewState().showToast("Отправлено успешно");
                } else {
                    getViewState().showError("Не удалось отправить сообщение!");
                }
            }

            @Override
            public void onFinishedWithException(WSException ex) {
                getViewState().showError(ex.getMessage());
            }
        });
    }

    //endregion

    //region OpenGate

    public void onOpenGateClick() {
        getViewState().showOpenGateDialog();
    }

    public void openGate(String gateNumber) {
        if (!TextUtils.isEmpty(gateNumber)) {
            try {
                int number = Integer.parseInt(gateNumber);
                getViewState().showProgress();
                if (number > 0) {
                    GateRequest request = new GateRequest(String.valueOf(number));
                    SapRequestUtils.openGate(request, new ServiceCallback() {
                        @Override
                        public void onEndedRequest() {
                            getViewState().hideProgress();
                        }

                        @Override
                        public void onFinished(String evParams) {
                            if (!evParams.equals(Constants.SAP_TRUE_FLAG))
                                getViewState().showError(R.string.open_gate_message_unsuccessful);
                            else {
                                getViewState().showMessage(R.string.open_gate_message_successful);
                            }
                        }

                        @Override
                        public void onFinishedWithException(WSException ex) {
                            getViewState().showError(ex.getMessage());
                        }

                        @Override
                        public void onCancelled() {
                            getViewState().hideProgress();
                        }
                    });
                }
            } catch (Exception ignored) {
                getViewState().showError(R.string.open_gate_message_error);
            }
        } else {
            getViewState().showError(R.string.open_gate_message_set_gate_number);
        }
    }

    public boolean isOpenGateEnable() {
        return DataRepository.getInstance().isGateKeeperEnabled();
    }

    //endregion

    //endregion

}
