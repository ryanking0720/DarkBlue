package com.DarkBlue.GUI;

import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Utilities;

import java.awt.Color;
import java.awt.Dimension;
//import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class RowBumper extends JPanel{

    private static final long serialVersionUID = Utilities.ONE_LONG;
    
    private final JPanel m_topBlank, m_bottomBlank;
    
    private final JPanel[] m_numbers;
    
    private final JLabel[] m_labels;

    public RowBumper(final ChessColor a_color){
        super(new GridLayout(Utilities.TEN, Utilities.ONE));
        
        m_topBlank = new JPanel();
        
        m_bottomBlank = new JPanel();
        
        m_topBlank.setBackground(Color.WHITE);
        m_bottomBlank.setBackground(Color.WHITE);
        
        m_numbers = new JPanel[Utilities.EIGHT];
        
        m_labels = new JLabel[Utilities.EIGHT];
        
        InitializeNumberedPanels();
        
        add(m_topBlank);
        
        InitializeLabels(a_color);
        
        AddNumberedPanels(a_color);
        
        //AdjustFonts();
        
        add(m_bottomBlank);
    }
    
    private void InitializeNumberedPanels(){
        for(int i = Utilities.ZERO; i < m_numbers.length; i++){
            m_numbers[i] = new JPanel();
            m_numbers[i].setSize(new Dimension(Utilities.SIXTY, Utilities.SIXTY));
            m_numbers[i].setBackground(Color.WHITE);
        }
    }
    
    private void AddNumberedPanels(final ChessColor a_color){
        for(int i = Utilities.ZERO; i < m_numbers.length; i++){
         
            if(a_color.IsWhite()){
                m_numbers[i].add(m_labels[i]);
            }else{
                m_numbers[i].add(m_labels[i]);
            }
            
            
        }
    }
    
    private void InitializeLabels(final ChessColor a_color){
        for(int i = Utilities.ZERO; i < m_numbers.length; i++){
            
            if(a_color.IsWhite()){
                m_labels[i] = new JLabel(Integer.toString(Utilities.EIGHT - i));
            }else{
                m_labels[i] = new JLabel(Integer.toString(Utilities.ONE + i));
            }
            
            m_numbers[i].add(m_labels[i]);
        }
    }
    
    // Source: https://stackoverflow.com/questions/2715118/how-to-change-the-size-of-the-font-of-a-jlabel-to-take-the-maximum-size
    /*
    private void AdjustFonts(){
        final Font labelFont = m_labels[Utilities.ZERO].getFont();
        final String labelText = m_labels[Utilities.ZERO].getText();

        final int stringWidth = m_labels[Utilities.ZERO].getFontMetrics(labelFont).stringWidth(labelText);
        final int componentWidth = m_labels[Utilities.ZERO].getWidth();

        // Find out how much the font can grow in width.
        final double widthRatio = (double)componentWidth / (double)stringWidth;

        final int newFontSize = (int)(labelFont.getSize() * widthRatio);
        final int componentHeight = m_labels[Utilities.ZERO].getHeight();

        // Pick a new font size so it will not be larger than the height of label.
        final int fontSizeToUse = Math.min(newFontSize, componentHeight);
        
        for(int i = Utilities.ZERO; i < m_labels.length; i++){
            // Set the label's font size to the newly determined size.
            m_labels[i].setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
        }
    }
    */
}