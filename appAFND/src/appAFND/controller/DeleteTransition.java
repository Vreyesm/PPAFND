/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appAFND.controller;

import java.util.ArrayList;
import javafx.scene.Node;

/**
 *
 * @author kirit
 */
public class DeleteTransition extends Command {

    private Node transition;
    private ArrayList<Command> commands;
    private ArrayList<Command> toRedo;

    public DeleteTransition(Node transition, ArrayList<Command> commands, ArrayList<Command> toRedo) {
        super(commands, toRedo);
        this.transition = transition;
    }

    @Override
    public void execute(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}