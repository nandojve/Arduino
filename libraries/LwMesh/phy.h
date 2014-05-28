#ifndef _PHY_TRAMPOLINE_H_
#define _PHY_TRAMPOLINE_H_

/*- Includes ---------------------------------------------------------------*/
#if defined(HAL_ATMEGA128RFA1)
	#include "phy/atmegarfa1/phy.h"
#elif defined(HAL_ATMEGA256RFR2)
	#include "phy/atmegarfr2/phy.h"
#else
	#error "Undefined Hardware Abstraction Layer (HAL)"
#endif

/*- Prototypes -------------------------------------------------------------*/

#endif // _PHY_TRAMPOLINE_H_