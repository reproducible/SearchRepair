void test(int __data_len, int req){
if ( blk_rq_bytes ( req       )    <  blk_rq_cur_bytes ( req       )       )     { blk_dump_rq_flags ( req       , "request botched"  )    ;   req - >  __data_len = blk_rq_cur_bytes ( req       )          ;  }    }