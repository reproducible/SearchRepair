int test(int rq, int errors, int q){
if ( unlikely ( blk_queue_dying ( q       )         )       )     {  rq - >   cmd_flags  REQ_QUIET ;   rq - >  errors = -   ENXIO ;  __blk_end_request_all ( rq       , rq    -  >       errors )    ;  spin_unlock_irq ( q    -  >       queue_lock )    ;  return      ;  }    }