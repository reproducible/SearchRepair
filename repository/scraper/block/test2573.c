void test(int bounce_pfn, int GFP_NOIO, int dma, int b_pfn, int bounce_gfp){
if ( dma     )     { init_emergency_isa_pool ( )    ;   q - >  bounce_gfp = GFP_NOIO   GFP_DMA      ;   q - >   limits  bounce_pfn = b_pfn        ;  }    }