void test(int rq, int ret, int BLKPREP_KILL){
if ( ret  ==  BLKPREP_KILL     )     {  rq - >   cmd_flags  REQ_QUIET ;  blk_start_request ( rq       )    ;  __blk_end_request_all ( rq       , -  EIO )    ;  }    }