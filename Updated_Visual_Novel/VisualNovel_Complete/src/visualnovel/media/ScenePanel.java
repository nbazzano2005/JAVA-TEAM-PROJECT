package visualnovel.media;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.*;

/** Original illustrated locations and expressive characters drawn with Java2D. */
public final class ScenePanel extends JPanel {
    private static final long serialVersionUID=1L;
    private String scene="start", location="classroom", speaker="You";
    /** Creates a scalable illustrated scene. */
    public ScenePanel() { setPreferredSize(new Dimension(800,250)); setBackground(new Color(0x222e43)); }
    /** Selects artwork appropriate to the current scene. */
    public void setScene(String id) { scene=id; repaint(); }
    /** Selects scene art and a character portrait from story metadata. */
    public void setScene(String id,String location,String speaker) { this.scene=id; this.location=location; this.speaker=speaker; repaint(); }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g=(Graphics2D)graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
            double scale=Math.min(getWidth()/960.0,getHeight()/280.0);
            g.translate((getWidth()-960*scale)/2,(getHeight()-280*scale)/2);g.scale(scale,scale);
            AffineTransform sceneTransform=g.getTransform();
            boolean park=(location.equals("park") || scene.equals("recreationPath")), night=(location.equals("bedroom") || scene.equals("evening"));
            boolean library=location.equals("library") || location.equals("office") || location.equals("lab") || scene.equals("studyPath") || scene.equals("planning");
            boolean maya=speaker.equals("Maya") || scene.equals("supportPath") || scene.equals("planning");
            if(location.equals("cafe")) cafe(g); else if(park) park(g); else if(library) library(g); else if(night) bedroom(g); else classroom(g);
            // A portrait shadow separates the character from the background.
            g.setColor(new Color(10,20,40,40)); g.fill(new Ellipse2D.Double(666,251,226,23));
            g.translate(0,scene.equals("failedEnding")?5:0);
            character(g,maya,scene.equals("failedEnding") || scene.equals("burnoutEnding"),scene.equals("passedEnding") || scene.equals("honorsEnding") || scene.equals("communityEnding"));
            g.setTransform(sceneTransform);
            String place=location.equals("cafe")?"CAMPUS CAFE":location.equals("office")?"OFFICE HOURS":location.equals("lab")?"PROJECT LAB":park?"CAMPUS GREEN":library?"SALMON LIBRARY":night?"AFTER HOURS":"CAMPUS CLASSROOM";
            pill(g,22,18,place,new Color(18,29,50,210));
            pill(g,718,240,speaker.equals("Professor Chen")?"PROFESSOR CHEN":speaker.equals("Leo")?"LEO  /  ROOMMATE":maya?"MAYA  /  CLASSMATE":"YOU  /  FINALS WEEK",new Color(18,29,50,225));
            if(scene.equals("passedEnding") || scene.equals("honorsEnding") || scene.equals("communityEnding")) confetti(g);
            if(scene.equals("failedEnding")) {
                g.setColor(new Color(36,45,83,60)); g.fillRect(0,0,960,280);
                pill(g,25,225,"A SETBACK. A NEW START.",new Color(35,42,66,230));
            }
            // Subtle frame makes the art read as one illustrated panel.
            g.setColor(new Color(255,255,255,90)); g.setStroke(new BasicStroke(2));
            g.drawRoundRect(1,1,957,277,18,18);
        } finally { g.dispose(); }
    }
    private static Color c(int rgb) { return new Color(rgb); }
    private static void rect(Graphics2D g,int color,int x,int y,int w,int h) { g.setColor(c(color));g.fillRect(x,y,w,h); }
    private static void round(Graphics2D g,int color,int x,int y,int w,int h,int r) { g.setColor(c(color));g.fillRoundRect(x,y,w,h,r,r); }
    private static void gradient(Graphics2D g,int top,int bottom) {
        g.setPaint(new GradientPaint(0,0,c(top),0,280,c(bottom)));g.fillRect(0,0,960,280);
    }
    private static void pill(Graphics2D g,int x,int y,String text,Color color) {
        g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,11));
        int width=g.getFontMetrics().stringWidth(text)+26;
        g.setColor(color);g.fillRoundRect(x,y,width,25,12,12);
        g.setColor(Color.WHITE);g.drawString(text,x+13,y+17);
    }
    private static void classroom(Graphics2D g) {
        gradient(g,0xe9dfce,0xb7c9c8);
        rect(g,0xc8b696,0,205,960,75);
        for(int x=0;x<960;x+=90) { g.setColor(c(0xb6a17f));g.drawLine(x,205,x-35,280); }
        round(g,0x836850,35,56,477,134,8);round(g,0x234a50,42,62,463,120,5);
        g.setPaint(new GradientPaint(45,60,c(0x35626a),500,180,c(0x18373f)));g.fillRect(46,66,455,112);
        g.setColor(c(0xe8f5e9));g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,25));g.drawString("MAKE YOUR NEXT MOVE.",65,103);
        g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,15));g.drawString("FINAL EXAM   /   WEDNESDAY",67,134);
        g.setColor(c(0xf2cf8a));g.drawString("Preparation + confidence + rest",67,160);
        rect(g,0xad9371,36,187,476,7);round(g,0xeee9db,410,181,34,5,3);
        for(int x=550;x<960;x+=170) {
            round(g,0xf4f0e6,x,22,140,166,5);
            g.setPaint(new GradientPaint(0,30,c(0x83b9d3),0,170,c(0xe6e9d5)));g.fillRect(x+8,30,124,150);
            g.setColor(c(0x91a8b1));g.fillPolygon(new int[]{x+8,x+40,x+90,x+132},new int[]{150,109,146,114},4);
            rect(g,0xe9e2d6,x+66,29,6,154);rect(g,0xe9e2d6,x+6,99,126,6);
        }
        // Empty foreground desks with books and warm wood highlights.
        for(int x=55;x<640;x+=220) {
            round(g,0x5f4e49,x+12,238,12,42,3);round(g,0x5f4e49,x+158,238,12,42,3);
            round(g,0x9d6c4d,x,224,187,20,6);rect(g,0xcc9867,x+4,224,179,4);
            round(g,0x52688c,x+20,212,56,11,3);rect(g,0xece7db,x+24,215,48,3);
        }
    }
    private static void library(Graphics2D g) {
        gradient(g,0xd5c7b5,0xa3b7bd);rect(g,0x9b8068,0,230,960,50);
        for(int sx=20;sx<620;sx+=193) {
            round(g,0x4a4146,sx,50,172,171,4);
            for(int row=0;row<3;row++) {
                int y=57+row*53;
                for(int b=0;b<10;b++) {
                    int[] colors={0x70869f,0xb4776b,0xc4a477,0x587c75,0x8c7a9f};
                    int h=29+(b*7+row*3)%14,x=sx+8+b*15;
                    round(g,colors[(b+row)%5],x,y+44-h,12,h,2);
                    rect(g,0xd7c7ac,x+3,y+48-h,6,2);
                }
                rect(g,0x9e7a58,sx+4,y+44,164,6);
            }
        }
        round(g,0xf2ddac,620,30,305,170,12);
        g.setPaint(new GradientPaint(620,30,c(0xf4d796),880,200,c(0xb5c7c0)));g.fillRect(629,39,287,152);
        rect(g,0xe3cb9b,759,38,7,154);
        round(g,0x805e4b,40,241,581,23,7);
        // Laptop, notebook, and coffee.
        round(g,0x303b51,250,165,142,80,8);round(g,0x8eafbb,257,172,128,64,4);
        g.setColor(c(0xe1e9dd));for(int i=0;i<4;i++)g.fillRoundRect(270,184+i*11,70+i%2*25,3,2,2);
        round(g,0x667080,237,242,168,6,3);
        round(g,0xe9dfc7,100,233,102,11,3);rect(g,0xaf7b68,149,233,3,11);
        round(g,0xd4a37e,445,206,25,36,6);g.setColor(c(0xd4a37e));g.setStroke(new BasicStroke(4));g.drawOval(462,214,16,17);
        g.setColor(new Color(255,255,255,110));g.draw(new QuadCurve2D.Double(455,202,443,187,456,181));
    }
    private static void park(Graphics2D g) {
        gradient(g,0x83bdd7,0xe9e4bf);
        g.setColor(new Color(255,240,190,130));g.fillOval(467,14,111,111);
        g.setColor(c(0xd4e6df));g.fillOval(82,53,170,33);g.fillOval(128,36,96,47);
        g.setColor(c(0x9db9a1));g.fill(new Ellipse2D.Double(-70,125,700,170));
        g.setColor(c(0x739b83));g.fill(new Ellipse2D.Double(350,137,730,170));
        g.setColor(c(0x537c68));g.fillRect(0,193,960,87);
        g.setColor(c(0xe4c8a1));g.fill(new Path2D.Double(){{moveTo(417,189);curveTo(337,215,355,235,285,280);lineTo(603,280);curveTo(474,231,505,212,444,189);closePath();}});
        for(int x:new int[]{50,250,852}) {
            rect(g,0x685849,x+34,80,18,132);
            g.setColor(c(0x427365));g.fillOval(x-5,30,103,106);
            g.setColor(c(0x659578));g.fillOval(x-22,52,85,69);
            g.setColor(c(0x7ca27d));g.fillOval(x+23,25,66,60);
        }
        for(int y=0;y<3;y++)round(g,0xa77c55,82,183+y*12,177,8,4);
        rect(g,0x4b5554,94,214,8,37);rect(g,0x4b5554,237,214,8,37);
        g.setColor(c(0xe4d2a2));for(int i=0;i<20;i++)g.fillOval((i*67)%650,225+(i*19)%48,3,3);
    }
    private static void cafe(Graphics2D g) {
        gradient(g,0xe5c7a2,0xb78d75);rect(g,0x836e66,0,221,960,59);
        for(int x=25;x<700;x+=215){round(g,0x79574c,x,44,180,132,8);round(g,0x8cb3b5,x+6,50,168,120,5);rect(g,0xc8b28d,x+87,50,5,120);}
        round(g,0x344d53,70,65,120,80,6);g.setColor(c(0xf3e7cf));g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,17));g.drawString("TAKE A",86,96);g.drawString("BREATHER",81,121);
        for(int x=80;x<650;x+=230){rect(g,0x4b464b,x+59,219,8,61);g.setColor(c(0xa57550));g.fillOval(x,202,128,27);round(g,0xf2d1a4,x+45,183,21,28,5);g.setColor(c(0xf2d1a4));g.setStroke(new BasicStroke(3));g.drawOval(x+60,189,14,13);}
        rect(g,0x5f4e48,0,0,960,8);for(int x=110;x<800;x+=240){rect(g,0x5f4e48,x,0,3,27);g.setColor(c(0xf0d19b));g.fillOval(x-17,23,38,18);}
    }
    private static void bedroom(Graphics2D g) {
        gradient(g,0x26314f,0x665575);rect(g,0x323a55,0,221,960,59);
        round(g,0x8e8a9a,50,50,260,150,9);round(g,0x18263f,58,58,244,134,5);
        g.setColor(c(0xe7dfa9));g.fillOval(235,73,36,36);g.setColor(c(0x18263f));g.fillOval(225,67,35,35);
        g.setColor(c(0xa7bed5));for(int i=0;i<20;i++)g.fillOval(70+(i*61)%217,68+(i*29)%106,2,2);
        rect(g,0x8e8a9a,175,55,6,140);
        round(g,0x4a526b,350,59,185,100,7);
        g.setColor(c(0xc7b9b0));g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,17));g.drawString("YOU'VE GOT THIS.",365,91);
        g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,13));g.drawString("One step at a time.",366,116);
        round(g,0x88665e,53,231,520,22,7);
        rect(g,0x696679,465,165,8,65);round(g,0xb9957b,429,145,79,29,10);
        g.setColor(new Color(255,216,151,30));g.fillPolygon(new int[]{430,508,577,360},new int[]{173,173,231,231},4);
        round(g,0x97849e,150,217,120,13,3);round(g,0xe6d2ad,164,208,110,8,3);
        round(g,0x31445f,315,182,63,49,5);g.setColor(c(0xcbb994));g.drawString("REST",324,211);
    }
    private void character(Graphics2D g,boolean maya,boolean sad,boolean happy) {
        // Character designed on a local coordinate system for consistent proportions.
        Graphics2D a=(Graphics2D)g.create();a.translate(770,6);
        try {
            Color outline=c(0x263149), skin=c(maya?0xc68d71:0xe7b394), shadow=c(maya?0xaf725f:0xce947b);
            a.setStroke(new BasicStroke(2.3f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            if(maya) { a.setColor(c(0x352c42));a.fillRoundRect(-61,27,122,176,65,65); }
            // Neck and shirt.
            a.setColor(shadow);a.fillRoundRect(-18,122,36,47,12,12);
            Path2D body=new Path2D.Double();body.moveTo(-21,146);body.curveTo(-77,150,-84,191,-91,268);body.lineTo(91,268);body.curveTo(84,191,77,150,21,146);body.closePath();
            a.setColor(c(speaker.equals("Leo")?0xb97650:speaker.equals("Professor Chen")?0x555374:maya?0x4d8c85:0x5369a8));a.fill(body);a.setColor(outline);a.draw(body);
            // Hoodie shadows and shoulders.
            a.setColor(c(speaker.equals("Leo")?0x8c513c:speaker.equals("Professor Chen")?0x3b3c56:maya?0x376c6b:0x3e5084));a.fill(new Polygon(new int[]{-63,-41,-38,-79},new int[]{171,183,268,268},4));
            a.fill(new Polygon(new int[]{63,41,38,79},new int[]{171,183,268,268},4));
            a.setColor(c(0xe6ddd0));a.fill(new Polygon(new int[]{-21,21,0},new int[]{148,148,189},3));
            a.setColor(outline);a.drawLine(-21,149,0,188);a.drawLine(21,149,0,188);
            a.setColor(c(maya?0x72aca0:0x7d90c0));a.setStroke(new BasicStroke(4));a.drawLine(-30,170,-34,218);a.drawLine(30,170,34,218);
            a.setColor(c(0xdadfcf));a.setStroke(new BasicStroke(2));a.drawLine(-12,175,-15,214);a.drawLine(12,175,15,214);
            // Face with ears and shaped jaw.
            a.setColor(shadow);a.fillOval(-54,79,18,29);a.fillOval(36,79,18,29);
            Path2D face=new Path2D.Double();face.moveTo(-44,63);face.curveTo(-49,107,-32,139,0,145);face.curveTo(32,139,49,107,44,63);face.curveTo(30,24,-31,24,-44,63);face.closePath();
            a.setColor(skin);a.fill(face);a.setColor(outline);a.draw(face);
            a.setColor(new Color(235,128,124,55));a.fillOval(-37,105,24,10);a.fillOval(14,105,24,10);
            // Swept hair silhouette and highlights.
            Path2D hair=new Path2D.Double();hair.moveTo(-47,90);hair.lineTo(-55,62);hair.lineTo(-47,34);hair.lineTo(-30,17);hair.lineTo(-7,22);hair.lineTo(12,10);hair.lineTo(23,24);hair.lineTo(43,24);hair.lineTo(56,52);hair.lineTo(48,91);hair.lineTo(36,62);hair.lineTo(12,51);hair.lineTo(-8,67);hair.lineTo(-6,48);hair.lineTo(-29,72);hair.lineTo(-29,54);hair.closePath();
            a.setColor(c(maya?0x352c42:0x34364b));a.fill(hair);a.setColor(outline);a.draw(hair);
            a.setColor(c(maya?0x665064:0x56546b));a.setStroke(new BasicStroke(3));a.draw(new QuadCurve2D.Double(-38,43,-14,26,2,34));a.draw(new QuadCurve2D.Double(10,31,32,30,39,46));
            // Large expressive eyes, colored irises, light reflections.
            for(int x:new int[]{-24,23}) {
                a.setColor(c(0xfff9ee));a.fillOval(x-11,84,22,15);
                a.setColor(c(maya?0x8b614b:0x49788b));a.fillOval(x-5,84,11,15);
                a.setColor(outline);a.fillOval(x-2,86,6,11);
                a.setColor(Color.WHITE);a.fillOval(x,86,3,4);
                a.setColor(outline);a.setStroke(new BasicStroke(2));a.drawArc(x-11,82,23,16,0,180);
                int lift=sad?(x<0?4:-4):0;
                a.drawLine(x-10,77+lift,x+9,75-lift);
            }
            a.setColor(shadow);a.draw(new QuadCurve2D.Double(0,97,-5,110,2,111));
            a.setColor(outline);
            if(sad)a.draw(new QuadCurve2D.Double(-9,128,0,120,10,128));
            else if(happy) {a.setColor(c(0x784a4d));a.fillArc(-13,116,26,20,180,180);a.setColor(c(0xfff5e4));a.fillRect(-9,126,18,3);}
            else a.draw(new QuadCurve2D.Double(-10,122,1,132,12,120));
            // Backpack strap and notebook held against chest.
            a.setStroke(new BasicStroke(8));a.setColor(c(0x293c56));a.drawLine(53,163,14,268);
            Graphics2D book=(Graphics2D)a.create();book.rotate(-0.12,0,230);
            round(book,maya?0xdfab72:0xdeb968,-36,207,73,73,5);rect(book,0xf2e4c7,-30,211,5,66);
            book.setColor(c(0x5e5960));book.setFont(new Font(Font.SANS_SERIF,Font.BOLD,10));book.drawString("CS / NOTES",-18,231);book.dispose();
            a.setColor(skin);a.fillRoundRect(22,235,29,17,12,12);a.setColor(outline);a.setStroke(new BasicStroke(1.4f));a.drawRoundRect(22,235,29,17,12,12);
            if(speaker.equals("Professor Chen")) {
                a.setColor(c(0x394050));a.setStroke(new BasicStroke(2));a.drawRoundRect(-39,81,32,23,8,8);a.drawRoundRect(8,81,32,23,8,8);a.drawLine(-7,89,8,89);
            }
            if(speaker.equals("Leo")) { round(a,0x284c5d,-51,31,102,27,12);round(a,0x284c5d,-54,52,117,8,5); }
            if(maya) { a.setColor(c(0xe6bd75));a.fillOval(-47,112,5,8);a.fillOval(42,112,5,8); }
        } finally { a.dispose(); }
    }
    private static void confetti(Graphics2D g) {
        int[] colors={0xefb86a,0x6db9b0,0xdc8a9c,0x8c9bd6};
        for(int i=0;i<30;i++) { Graphics2D a=(Graphics2D)g.create();a.translate(26+(i*137)%900,12+(i*43)%220);a.rotate(i*0.7);a.setColor(c(colors[i%4]));a.fillRoundRect(0,0,4,11,2,2);a.dispose(); }
        pill(g,25,225,"CHAPTER COMPLETE  /  YOU PASSED",new Color(33,91,81,235));
    }
}
