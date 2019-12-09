package com.h3.reservation.slack.fragment.block;

import com.h3.reservation.slack.fragment.element.Element;

import java.util.List;

/**
 * @author heebg
 * @version 1.0
 * @date 2019-12-04
 */
public class ActionsBlock extends Block {
    private String block_id;
    private List<Element> elements;

    public ActionsBlock() {
        super(BlockType.ACTIONS);
    }

    public ActionsBlock(String block_id, List<Element> elements) {
        super(BlockType.ACTIONS);
        this.block_id = block_id;
        this.elements = elements;
    }

    public List<Element> getElements() {
        return elements;
    }
}
