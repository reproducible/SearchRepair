void test(int hang_check, int ait){
if ( hang_check     )     while ( ait_for_completion_io_timeout ( ait       , hang_check   *  ( HZ   /  2     )      )         )    ;    else wait_for_completion_io ( ait       )    ;    }