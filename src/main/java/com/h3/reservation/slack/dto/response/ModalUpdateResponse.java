package com.h3.reservation.slack.dto.response;

import com.h3.reservation.slack.fragment.view.ModalView;

public class ModalUpdateResponse {
    private final String responseAction = "update";
    private ModalView view;

    public ModalUpdateResponse() {
    }

    public ModalUpdateResponse(ModalView view) {
        this.view = view;
    }

    public String getResponseAction() {
        return responseAction;
    }

    public ModalView getView() {
        return view;
    }
}
