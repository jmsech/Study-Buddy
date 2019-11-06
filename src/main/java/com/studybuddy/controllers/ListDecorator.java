package com.studybuddy.controllers;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.UnaryOperator;

public class ListDecorator<T> implements List<T> {

    protected List<T> arr;

    public ListDecorator (List<T> list) {
        super();
        this.arr = list;
    }

    public T sample() {
        int length = this.arr.size();
        int num = (int) (Math.random() * length);
        return (T) this.arr.get(num);
    }

    public int size() { return this.arr.size(); }
    public boolean isEmpty() {return this.arr.isEmpty(); }
    public void replaceAll(UnaryOperator<T> operator) { this.arr.replaceAll(operator); }
    public void sort(Comparator<? super T> c) { this.arr.sort(c); }
    public Spliterator<T> spliterator() { return this.arr.spliterator(); }
    public boolean contains(Object o) { return this.arr.contains(o); }
    public Iterator<T> iterator() { return this.arr.iterator(); }
    public Object[] toArray() { return this.arr.toArray(); }
    public <T1> T1[] toArray(@NotNull T1[] a) { return this.arr.toArray(a); }
    public boolean add(T t) { return this.arr.add(t); }
    public boolean remove(Object o) { return this.arr.remove(o); }
    public boolean containsAll(@NotNull Collection<?> c) { return this.arr.containsAll(c); }
    public boolean addAll(@NotNull Collection<? extends T> c) { return this.arr.addAll(c); }
    public boolean addAll(int index, @NotNull Collection<? extends T> c) { return this.arr.addAll(index, c); }
    public boolean removeAll(@NotNull Collection<?> c) { return this.arr.removeAll(c); }
    public boolean retainAll(@NotNull Collection<?> c) { return this.arr.retainAll(c); }
    public void clear() { this.arr.clear(); }
    public T get(int index) { return this.arr.get(index); }
    public T set(int index, T element) { return this.arr.set(index, element); }
    public void add(int index, T element) { this.add(index,element); }
    public T remove(int index) { return this.remove(index); }
    public int indexOf(Object o) { return this.arr.indexOf(o); }
    public int lastIndexOf(Object o) { return this.arr.lastIndexOf(o); }
    public ListIterator<T> listIterator() { return this.arr.listIterator(); }
    public ListIterator<T> listIterator(int index) { return this.arr.listIterator(index); }
    public List<T> subList(int fromIndex, int toIndex) { return subList(fromIndex, toIndex); }
}
