void test(int new_cfqq, int cfqq){
if ( cfqq     )     {  struct  cfq_queue *   new_cfqq ;  new_cfqq = cfq_get_queue ( cfqd       , BLK_RW_ASYNC       , cic       , bio       , GFP_ATOMIC       )          ;  if ( new_cfqq     )     {  cic - >   cfqq [   BLK_RW_ASYNC ] =   new_cfqq ;  cfq_put_queue ( cfqq       )    ;  }      }    }