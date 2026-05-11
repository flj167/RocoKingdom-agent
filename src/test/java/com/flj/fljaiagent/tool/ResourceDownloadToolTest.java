package com.flj.fljaiagent.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceDownloadToolTest {

    @Test
    void download() {
        ResourceDownloadTool tool = new ResourceDownloadTool();
        String baiduUrl="https://ts3.tc.mm.bing.net/th/id/OIP-C.sVwROu3b62Btk4AQuIz45gHaDi";
        String baiduLogo = tool.download(baiduUrl, "baiduLogo.png");
        assertNotNull(baiduLogo);
    }
}