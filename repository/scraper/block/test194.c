int test(int bio, int NULL){
if ( bio_integrity ( bio_src       )       )     { int  ret  ;  ret = bio_integrity_clone ( bio       , bio_src       , gfp_mask       )          ;  if ( ret  <  0     )     { bio_put ( bio       )    ;  return NULL       ;  }      }    }