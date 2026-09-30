class Lab03 {
    public static void main(String[] args) {
        // Test your code here...
    }
}

interface MyList<E> {

    /**
     * Returns the number of elements in the list.
     */
    int size();

    /**
     * Returns true if the list is empty.
     */
    boolean isEmpty(); 

    /**
     * Removes all elements from the list.
     */
    void clear();

    /**
     * Inserts an element at the beginning of the list.
     */
    void addFirst(E e);

    /**
     * Inserts an element at the end of the list.
     */
    void addLast(E e);

    /**
     * Inserts an element at a given index (0..size).
     */
    void insertAt(int index, E e);

    /**
     * Removes and returns the first element, or null if empty.
     */
    E removeFirst();

    /**
     * Removes and returns the last element, or null if empty.
     */
    E removeLast();

    /**
     * Removes and returns the element at a given index.
     * Returns null if index is invalid.
     */
    E removeAt(int index);

    /**
     * Returns the element at a given index, or null if invalid.
     */
    E get(int index);

    /**
     * Replaces the element at a given index with a new value.
     * Returns the old value, or null if index is invalid.
     */
    E set(int index, E e);

    /**
     * Returns the index of the first occurrence of the given object,
     * or -1 if not found.
     */
    int indexOf(E o);

    /**
     * Removes the first occurrence of the given object.
     * Returns true if removed, false otherwise.
     */
    boolean remove(E o);
}


class Node<E> {
    public E item;
    public Node<E> next;

    public Node(E item, Node<E> next) {
        this.item = item;
        this.next = next;
    }
}

class SinglyLinkedList<E> implements MyList<E> {
private Node <E>head;
private Node <E>tail;
private int size=0;
    public Node<E> getHead() {
        // Do not modify this method
        return head;
    }
    public Node<E> getTail(){
        return tail;
    }
    
    
   @Override
   public int size(){
        return size;
    }
    
     @Override
     public boolean isEmpty()
     {
         return size==0;
     }
     
   @Override public void clear()
   {
       head=null;
       tail=null;
       size=0;
   }
   
   @Override public void addFirst(E e) {
        Node<E> newNode=new Node<>(e, head);
        head=newNode;
        if (tail== null)
        {
            tail=head;
            
        }
        size++;
    }
    
   
    @Override public void addLast(E e)
    {
        Node<E> newNode=new Node<>(e, null);
        if (isEmpty())
        {
            head=newNode;
        }
        else
        {
            tail.next=newNode;
        }
        tail=newNode;
        size++;
    }
    
    @Override public void insertAt(int index, E e) {
        if (index <0||index>size) return;
        if (index==0)
        {
            addFirst(e);
        }
        
        else if (index==size)
        {
        addLast(e);
        }
        else
        
        {
        Node<E> current=head;
        for(int i=0;i<index-1;i++)
        {
            current=current.next;
        }
            
        Node<E> newNode=new Node<>(e, current.next);
        current.next=newNode;
        size++;
        }
    }
    
    @Override public E removeFirst() {
        if (isEmpty())
        {
            return null;
        }
        
        E oldItem=head.item;
        head=head.next;
        if (head==null)
        {
            tail=null;
        }
        size--;
        return oldItem;
    }
    @Override public E removeLast() {
        if (isEmpty())
        {
            return null;
        }
        
        if (size==1)
        {
            return removeFirst();
        }
        
        Node<E> current=head;
        while (current.next !=tail)
        {
            current=current.next;
            
        }
            
        E oldItem=tail.item;
        tail=current;
        tail.next=null;
        size--;
        return oldItem;
    }
    
    @Override public E removeAt(int index)
    {
        if (index<0||index>= size)
        {
            return null;
        }
        if (index==0)
        {
            return removeFirst();
        }
        Node<E> current=head;
        for (int i =0;i<index -1; i++)
        {
            current=current.next;
        }
        Node<E> nodeToRemove=current.next;
        E oldItem=nodeToRemove.item;
        current.next=nodeToRemove.next;
        if (nodeToRemove==tail)
        {
            tail=current;
        }
        size--;
        return oldItem;
    }
    @Override
    public E get(int index)
    {
    if (index < 0 || index >=size)
    {
        return null; 
    }
    Node<E> current=head;
    for (int i =0;i <index; i++)
    {
        current=current.next;
    }
    return current.item;
}

    @Override
    public E set(int index, E e) {
        if (index < 0 ||index >= size)
        {
            return null;
        }
        Node<E> current =head;
    for (int i = 0; i < index; i++)
    {
        current = current.next;
    }
    E old =current.item;
    current.item =e;
    return old;
}
    
    @Override
    public int indexOf(E o)
    {
        Node<E> current =head;
        if (o == null)
        {
            for (int i=0; i<size; i++)
            {
                if (current.item==null) return i;
                current=current.next;
            }
        } else {
            for (int i =0; i<size; i++) 
            {
                if (o.equals(current.item)) return i;
                current=current.next;
            }
        }
        return -1;
    }
    
    @Override
    public boolean remove(E o) {
        Node<E>current=head,prev =null;
        if (o ==null)
        {
            for (int i= 0; i<size;i++)
            {
                if (current.item == null)
                {
                    if (prev == null)
                    {
                        removeFirst();
                    }
                    else
                    {
                        prev.next=current.next;
                        if (current == tail)
                        {
                            tail=prev;
                        }
                        size--;
                    }
                    return true;
                }
                prev=current;
                current=current.next;
            }
        } 
        else
        {
            for (int i=0; i<size; i++)
            {
                if (o.equals(current.item))
                {
                    if (prev == null) 
                    
                    {
                        removeFirst();
                    }
                    else
                    {
                        prev.next=current.next;
                        
                        if (current ==tail)
                        {
                            tail =prev;
                        }
                        size--;
                    }
                    return true;
                }
                prev=current;
                current=current.next;
            }
        }
        return false;
    }

    
}

class CircularLinkedList<E> implements MyList<E> {

    private Node<E> head;
    private Node<E> tail;
    private int size=0;
    
    public CircularLinkedList(){
    }
    
    public Node<E> getTail() {
        // Do not modify this method
        return tail;
    }
    
    
    @Override
    public void clear()
    {
        head=null;
        tail=null;
        size=0;
    }

    
    @Override
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

   

     @Override
    public void addFirst(E e) {
        Node<E> newNode=new Node<>(e, null);
        if (isEmpty()) {
            head = tail = newNode;
            tail.next = head; 
        } else {
            newNode.next = head;
            head = newNode;
            tail.next = head; 
        }
        size++;
    }

    @Override
    public void addLast(E e) {
        Node<E> newNode=new Node<>(e, null);
        if (isEmpty()) {
            head=tail=newNode;
            tail.next=head;
        } 
        else
        {
            tail.next=newNode;
            tail=newNode;
            tail.next =head;
        }
        size++;
    }
    

    @Override
    public void insertAt(int index, E e)
    {
        if (index <0||index>size) return;
        
        if (index==0)
        {
            addFirst(e);
        } 
        else if (index ==size)
        {
            addLast(e);
        }
        
        else
        {
             Node<E> newNode=new Node<>(e, null);
            Node<E> current=head;
            for (int i=0;i <index-1; i++)
            {
                current=current.next;
            }
            newNode.next=current.next;
            current.next=newNode;
            size++;
        }
    }
    
    @Override
    public E removeFirst()
    {
        if (isEmpty()) return null;
        E removed=head.item;
        
        if (size==1)
        {
            head=tail=null;
        }
        else
        {
            head=head.next;
            tail.next=head;
        }
        size--;
        return removed;
    }

    @Override
    public E removeLast()
    {
        if (isEmpty()) return null;
        E removed=tail.item;
        if (size==1)
        {
            head=tail=null;
        }
        else
        {
            Node<E> current=head;
            while (current.next !=tail)
            {
                current=current.next;
            }
            current.next=head;
            tail=current;
        }
        size--;
        return removed;
    }

    @Override
    public E removeAt(int index)
    {
        if (index < 0||index >= size) return null;
        if (index == 0) return removeFirst();
        if (index==size-1) return removeLast();

        Node<E> current=head;
        
        for (int i=0;i<index-1; i++)
        {
            current=current.next;
        }
        E removed=current.next.item;
        current.next=current.next.next;
        size--;
        return removed;
    }

    @Override
    public boolean remove(E e) {
        if (isEmpty()) return false;

        if (head.item.equals(e)) {
            removeFirst();
            return true;
        }

        Node<E> current=head;
        do {
            
            if (current.next.item.equals(e))
            {
                if (current.next==tail)
                {
                    removeLast();
                }
                else
                {
                    current.next=current.next.next;
                    size--;
                }
                return true;
            }
            current=current.next;
        }
        while (current!= head);

        return false;
    }
    
    @Override
    public E get(int index){
        if (index <0||index >= size) return null;
        Node<E> current=head;
        for (int i =0;i< index; i++)
        {
            current=current.next;
        }
        return current.item;
    }

    @Override
    public E set(int index, E e) {
        if (index < 0||index >=size) return null;
        Node<E> current=head;
        for (int i =0;i <index; i++)
        {
            current=current.next;
        }
        E old=current.item;
        current.item=e;
        return old;
    }
   
   
   @Override
   public int indexOf(E e)
   {
        if (isEmpty()) return -1;
        Node<E> current=head;
        int index=0;
        do {
            if (current.item.equals(e))
            {
                return index;
                
            }
            current=current.next;
            index++;
        }
        while(current != head);
        return -1;
    }
   
   
   
   
   
    }
    
    



class DNode<E> {
    public E item;
    public DNode<E> prev;
    public DNode<E> next;

    public DNode(E item, DNode<E> prev, DNode<E> next) {
        this.item = item;
        this.prev = prev;
        this.next = next;
    }
}

class DoublyLinkedList<E> implements MyList<E> {

    public DNode<E> getHead() {
        // Do not modify this method
        return head;
    }

    public DNode<E> getTail() {
        // Do not modify this method
        return tail;
    }
    
    
    private DNode<E> head, tail;
    private int size;
    @Override
    public boolean isEmpty()
    {
        return size==0;
        
    }

    @Override
public void clear() {
    DNode<E> current=head;
    while (current !=null) {
        DNode<E> next =current.next;
        current.prev =null;
        current.next =null;
        current.item =null;
        current = next;
    }
    head=tail= null;
    size=0;
}
    
    @Override
    public int size()
    {
        return size;
        
    }
    @Override
    public void addFirst(E e) {
        DNode<E> newNode=new DNode<>(e, null, null);
        if (isEmpty()) {
            head=tail=newNode;
        }
        else
        {
            newNode.next=head;
            head.prev=newNode;
            head=newNode;
        }
        size++;
    }

    @Override
    public void addLast(E e)
    {
        DNode<E> newNode=new DNode<>(e, null, null);
        if (isEmpty()) {
            head=tail=newNode;
        } 
        else
        {
            tail.next=newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    @Override
    public void insertAt(int index, E e) {
        if (index < 0|| index>size) return;
        if (index ==0)
        {
            addFirst(e); return;
            
        }
        if (index == size)
        {
            addLast(e);
            return;
            
        }

        DNode<E> newNode=new DNode<>(e, null, null);
        DNode<E> current=head;
        for (int i =0;i <index; i++)
        {
            current=current.next;
        }
        newNode.prev=current.prev;
        newNode.next=current;
        current.prev.next=newNode;
        current.prev = newNode;
        size++;
    }

    @Override
    public E removeFirst() {
        if (isEmpty()) return null;
        E val=head.item;
        if (head== tail)
        {
            head=tail = null;
        }
        else
        {
            head=head.next;
            head.prev = null;
        }
        size--;
        return val;
    }

    @Override
    public E removeLast() {
        if (isEmpty()) return null;
        
        E val=tail.item;
        if (head==tail)
        {
            head=tail=null;
        }
        else
        {
            tail=tail.prev;
            tail.next=null;
        }
        size--;
        return val;
    }
    
    @Override
    public E removeAt(int index)
    {
        if (index < 0||index >= size) return null;
        if (index==0) return removeFirst();
        if (index==size-1) return removeLast();

        DNode<E> current =head;
        for (int i=0; i< index;i++)
        {
            current=current.next;
        }
        current.prev.next=current.next;
        current.next.prev=current.prev;
        size--;
        return current.item;
    }
    
    @Override
    public boolean remove(E e)
    {
        int index=indexOf(e);
        if (index== -1)return false;
        removeAt(index);
        
        return true;
    }
    @Override
    public E set(int index, E e) {
        if (index< 0||index>= size)return null;
        DNode<E> current=head;
        for (int i =0;i < index;i++)
        {
            current = current.next;
        }
        
        E old=current.item;
        current.item=e;
        return old;
    }

    @Override
    public E get(int index)
    {
        if (index< 0 ||index >= size) return null;
        DNode<E> current =head;
        for (int i =0; i<index;i++)
        {
            current=current.next;
        }
        return current.item;
    }
    @Override
    public int indexOf(E e)
    {
        DNode<E> current=head;
        int index=0;
        while (current != null)
        {
            if ((e == null && current.item == null)||(e != null && e.equals(current.item)))
            {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }
    
    
    
    

}
