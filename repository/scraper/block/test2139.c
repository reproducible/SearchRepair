int test(int missing, int rq, int ata, int BLK_MQ_TAG_FAIL, int tag, int cmd_flags, int data, int REQ_MQ_INFLIGHT){
if ( tag  !=  BLK_MQ_TAG_FAIL     )     { rq = data    -  >        <missing ';'>   hctx - >   tags - >   rqs [   tag ] ;  if ( blk_mq_tag_busy ( data    -        <missing ')'>    >  hctx     )  )    {  rq - >  cmd_flags = REQ_MQ_INFLIGHT        ;  atomic_inc ( ata    -  >          <missing ';'>   hctx - >   nr_active ) ;  }       rq - >  tag = tag        ;  blk_mq_rq_ctx_init ( data    -  >          <missing ';'>   q ,   data - >   ctx ,   rq ,   rw ) ;  return rq       ;  }    }