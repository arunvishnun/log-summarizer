import java.util.Date;

// Class for insertion operation to AVL Tree. So nodes can be traversed in order - sorted order.
class AVLTree {

    Timestamp root;

    // A utility function to get height of the tree
    int height(Timestamp N) {
        if (N == null)
            return 0;

        return N.height;
    }

    // A utility function to get maximum of two integers
    int max(int a, int b) {
        return (a > b) ? a : b;
    }

    // A utility function to right rotate subtree rooted with y
    Timestamp rightRotate(Timestamp y) {
        Timestamp x = y.left;
        Timestamp T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;

        // Return new root
        return x;
    }

    // A utility function to left rotate subtree rooted with x
    Timestamp leftRotate(Timestamp x) {
        Timestamp y = x.right;
        Timestamp T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        //  Update heights
        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;

        // Return new root
        return y;
    }

    // Get Balance factor of Timestamp N
    int getBalance(Timestamp N) {
        if (N == null)
            return 0;

        return height(N.left) - height(N.right);
    }

    Timestamp insert(Timestamp t, String timestamp, String host) {

        // Perform the normal BST insertion.
        if (t == null)
            return (new Timestamp(timestamp, host));
        
        Date date1 = Util.convertToDate(timestamp);
        Date date2 = Util.convertToDate(t.getCurrentWindow());

        if (date1.compareTo(date2) > 0) {
        	t.right = insert(t.right, timestamp, host);
        } else if (date1.compareTo(date2) < 0) {
        	t.left = insert(t.left, timestamp, host);
        } else if (date1.compareTo(date2) == 0) {
           // do comparisons for host
        	if( host.compareTo(t.getHost()) > 0 ) {
        		t.right = insert(t.right, timestamp, host);
        	} else if(host.compareTo(t.getHost()) < 0 ) {
        		t.left = insert(t.left, timestamp, host);
        	} else {
        		return t;
        	}
        }
        
        // Update height of this ancestor Timestamp
        t.height = 1 + max(height(t.left),
                              height(t.right));

        // Get the balance factor of this ancestor Timestamp node to check whether this Timestamp became unbalanced
        int balance = getBalance(t);

        // If this Timestamp becomes unbalanced, then there are 4 cases Left Left Case
        if ( balance > 1 && (timestamp.compareTo(t.left.getCurrentWindow()) < 0) )
            return rightRotate(t);

        // Right Right Case
        if (balance < -1 && (timestamp.compareTo(t.right.getCurrentWindow()) > 0) )
            return leftRotate(t);

        // Left Right Case
        if (balance > 1 && (timestamp.compareTo(t.left.getCurrentWindow()) > 0) ) {
            t.left = leftRotate(t.left);
            return rightRotate(t);
        }

        // Right Left Case
        if (balance < -1 && (timestamp.compareTo(t.right.getCurrentWindow()) < 0) ) {
            t.right = rightRotate(t.right);
            return leftRotate(t);
        }

        // return the (unchanged) Timestamp pointer.
        return t;
    }
}