int test(int CPU_ONLINE_FROZEN, int CPU_DEAD_FROZEN, int CPU_ONLINE, int CPU_DEAD, int action){
if ( action  ==  CPU_DEAD    || action  ==  CPU_DEAD_FROZEN      )     return blk_mq_hctx_cpu_offline ( hctx       , cpu       )         ;    else if ( action  ==  CPU_ONLINE    || action  ==  CPU_ONLINE_FROZEN      )     return blk_mq_hctx_cpu_online ( hctx       , cpu       )         ;     }