package com.quizzy.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Paginator<T> {

    private int currentPage;
    private int pageSize;
    private List<T> items;

    public Paginator(int pageSize) {
        this.pageSize = Math.max(1, pageSize);
        this.currentPage = 1;
        this.items = new ArrayList<>();
    }

    public void setItems(List<T> filtered) {
        this.items = (filtered != null) ? new ArrayList<>(filtered) : new ArrayList<>();
        this.currentPage = 1;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setPage(int page) {
        int totalPages = getTotalPages();
        if (page > totalPages) {
            this.currentPage = totalPages;
        } else if (page < 1) {
            this.currentPage = 1;
        } else {
            this.currentPage = page;
        }
    }

    public boolean nextPage() {
        if (currentPage < getTotalPages()) {
            currentPage++;
            return true;
        }
        return false;
    }

    public boolean prevPage() {
        if (currentPage > 1) {
            currentPage--;
            return true;
        }
        return false;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = Math.max(1, pageSize);
        setPage(currentPage);
    }

    public int getTotalItems() {
        return items.size();
    }

    public int getTotalPages() {
        if (items.isEmpty()) {
            return 1;
        }
        return Math.max(1, (int) Math.ceil((double) items.size() / pageSize));
    }

    public List<T> getCurrentPageItems() {
        int total = items.size();
        if (total == 0) {
            return Collections.emptyList();
        }
        int totalPages = getTotalPages();
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }

        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        if (fromIndex < total) {
            return items.subList(fromIndex, toIndex);
        }
        return Collections.emptyList();
    }

    public String getPaginationInfoText(String itemLabel) {
        int total = items.size();
        if (total == 0) {
            return String.format("Showing 0 to 0 of 0 %s", itemLabel);
        }
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        return String.format("Showing %d to %d of %d %s", (fromIndex + 1), toIndex, total, itemLabel);
    }
}
