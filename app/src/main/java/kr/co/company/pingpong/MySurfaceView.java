package kr.co.company.pingpong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

class Ball{
    int x, y, xInc = 1, yInc = 1;
    int diameter;
    static int WIDTH = 1080, HEIGHT = 1920;

    public Ball(int d){
        this.diameter = d;

        //볼의 위치를 랜덤하게 설정
        x = (int) (Math.random() * (WIDTH - d)+3);
        y = (int) (Math.random() * (HEIGHT - d)+3);

        //한번에 움직이는 거리도 랜덤하게 설정
        xInc = (int) (Math.random() * 5+1);
        yInc = (int) (Math.random() * 5+1);
    }

    // 여기서 공을 그린다.
    public void paint(Canvas g){
        Paint paint = new Paint();

        //벽에 부딪히면 반사하게 한다.
        if(x<0||x> (WIDTH - diameter))
            xInc = -xInc;
        if(y<0||y> (HEIGHT - diameter))
            yInc = -yInc;

        //볼의 좌표를 갱신하다.
        x += xInc;
        y += yInc;

        //볼을 화면에 그린다.
        paint.setColor(Color.RED);
        g.drawCircle(x,y,diameter,paint);

    }
}
// 서피스 뷰 정의
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    public Ball basket[] = new Ball[10];
    private MyThread thread;

    public MySurfaceView(Context context){
        super(context);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new MyThread(holder);

        // Ball 객체를 생성하여서 배열에 넣는다.
        for(int i = 0; i<10; i++)
            basket[i]= new Ball(20);
    }

    public MyThread getThread(){
        return thread;
    }

    public void surfaceCreated(SurfaceHolder holder){
        //스레드를 시작한다.
        thread.setRunning(true);

        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;

        // 스레드를 중지한다.
        thread.setRunning(false);
        while (retry){
            try{
                thread.join();
                retry = false;
            }catch (InterruptedException e){
            }
        }
    }

    public class MyThread extends Thread {

        private boolean mRun = false;
        private SurfaceHolder mSurfaceHolder;

        public MyThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    c.drawColor(Color.BLACK);
                    synchronized (mSurfaceHolder) {
                        for (Ball b : basket) {
                            b.paint(c);
                        }
                    }
                } finally {
                    if (c != null) {
                        //캔버스의 로킹을 푼다.
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
                //try{Thread.sleep(100)} catch(InterruptedException e){}
            }
        }

        public void setRunning(boolean b) {
            mRun = b;
        }
    }
}
