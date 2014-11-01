package retrieval.testvector;

/**
 * This class implement a Test information structure
 * @author Loic Rollus
 **/
public class TestPoint
{
        /** Point to test **/
        private PatchPoint point;

        /** Value to compare **/
        private double value;

        /** Information to Test (R,G,B,H,S,V,...) **/
        private int position;

        /**
         * This class is an ArrayList that can contains TestVector objetcs
         * @param point Point test
         * @param value Value to compare
         * @param position Position of data to compare (r=0,g,b,h,s,v=5)
         */
        public TestPoint(PatchPoint point,double value, int position)
        {
            this.point = point;
            this.value = value;
            this.position = position;
        }

        /**
         * Getter of X
         * @return X
         **/
        public int getX()
        {
            return point.getX();
        }

        /**
         * Getter of Y
         * @return Y
         **/
        public int getY()
        {
            return point.getY();
        }

        /**
         * Getter of the point
         * @return Point of the test
         **/
        public PatchPoint getPoint()
        {
            return point;
        }

        /**
         * Getter of the value
         * @return Value of the test
         **/
        public double getValue()
        {
            return value;
        }

        /**
         * Getter of the position
         * @return Position of information to compare
         **/
        public int getPosition()
        {
            return position;
        }
}