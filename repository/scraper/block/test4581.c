void test(int missing, int timed_out, int cfqq, int fqd){
if ( cfqq     )     { timed_out = 0        ;  if ( cfq_cfqq_must_dispatch ( cfqq       )       )          goto  out_kick ;  if ( cfq_slice_used ( cfqq       )       )          goto  expire ;  if ( fqd   - >   busy_queues )          goto  out_cont ;  if ( B_EMPTY_ROOT ( fqq    -        <missing ')'>    >  sort_list     )  )         goto  out_kick ;  cfq_clear_cfqq_deep ( cfqq       )    ;  }    }