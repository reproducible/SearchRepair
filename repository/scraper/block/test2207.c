void test(int rq, int missing, int reserved){
if ( time_after_eq ( jiffies       , rq    -        <missing ')'>    >  deadline     )  )    { if ( lk_mark_rq_complete ( rq       )       )     blk_mq_rq_timed_out ( rq       , reserved       )    ;      }    }