package com.h3.reservation.slack.dto.request.viewsubmission.values.block;

import com.h3.reservation.slack.fragment.composition.Option;

public class StartTime {
    private Option selected_option;

    public StartTime() {
    }

    public StartTime(Option selected_option) {
        this.selected_option = selected_option;
    }

    public Option getSelected_option() {
        return selected_option;
    }
}
