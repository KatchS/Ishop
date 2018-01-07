package com.sk.ishop.main_screen;

/**
 * Created by sk on 19/12/2017.
 */

public interface ScanListener {

    /**
     * notify that the barcode scanner is opened
     */
    void scannerOpened();

    /**
     * notify that the barcode scanner is closed
     */
    void scannerClosed();
}
